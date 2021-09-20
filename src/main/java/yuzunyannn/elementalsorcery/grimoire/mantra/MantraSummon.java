package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.ConditionEffect;
import yuzunyannn.elementalsorcery.grimoire.WantedTargetResult;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.tool.ItemSoulWoodSword;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectSummonRender;
import yuzunyannn.elementalsorcery.summon.Summon;
import yuzunyannn.elementalsorcery.summon.recipe.SummonRecipe;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class MantraSummon extends MantraCommon {

	static public void summon(World world, BlockPos pos, EntityLivingBase summoner, ItemStack keepsake,
			SummonRecipe summonRecipe) {
		if (world.isRemote) return;
		EntityGrimoire grimoire = new EntityGrimoire(world, summoner, ESInit.MANTRAS.SUMMON, null,
				EntityGrimoire.STATE_AFTER_SPELLING);
		Data data = (Data) grimoire.getMantraData();
		data.keepsake = keepsake;
		data.summonRecipe = summonRecipe;
		data.power = 100;
		data.pos = pos;
		data.summon = data.summonRecipe.createSummon(data.keepsake, data.world, data.pos);
		grimoire.setPosition(data.pos.getX(), data.pos.getY(), data.pos.getZ());
		world.spawnEntity(grimoire);
	}

	public static class Data extends MantraDataCommon {

		public final World world;
		// 保存部分
		protected BlockPos pos;
		protected int power = 0; // 积攒的能量
		protected SummonRecipe summonRecipe = null;// 召唤方案
		protected ItemStack keepsake = ItemStack.EMPTY;
		// 还原部分
		protected Summon summon = null; // 召唤句柄

		public Data(World world) {
			this.world = world;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			if (pos == null) return nbt;
			NBTHelper.setBlockPos(nbt, "pos", pos);
			nbt.setByte("power", (byte) power);
			nbt.setTag("keepsake", keepsake.serializeNBT());
			if (summonRecipe != null) nbt.setString("summon", summonRecipe.getRegistryName().toString());
			if (summon != null) nbt.setTag("sData", summon.serializeNBT());
			return nbt;
		}

		@Override
		public NBTTagCompound serializeNBTForSend() {
			return this.serializeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			power = nbt.getByte("power");
			if (NBTHelper.hasBlockPos(nbt, "pos")) pos = NBTHelper.getBlockPos(nbt, "pos");
			if (pos == null) return;
			keepsake = new ItemStack(nbt.getCompoundTag("keepsake"));
			summonRecipe = SummonRecipe.get(nbt.getString("summon"));
			if (summonRecipe != null) summon = summonRecipe.createSummon(keepsake, world, pos);
			if (summon != null && nbt.hasKey("sData", NBTTag.TAG_COMPOUND))
				summon.deserializeNBT(nbt.getCompoundTag("sData"));
		}

		public Summon getSummon() {
			return summon;
		}

		public boolean isEmpty() {
			return pos == null || summon == null;
		}
	}

	public MantraSummon() {
		this.setUnlocalizedName("summon");
		this.setColor(0xda003e);
		this.setIcon("summon");
		this.setRarity(2);
		this.setOccupation(8);
	}

	@Override
	public IMantraData getData(NBTTagCompound metaData, World world, ICaster caster) {
		return new Data(world);
	}

	@Override
	public void startSpelling(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		data.markContinue(false);
		Entity entity = caster.iWantCaster().asEntity();
		if (entity == null) return;
		// 寻找召唤任务
		if (entity instanceof EntityLivingBase) {
			ItemStack stack = ((EntityLivingBase) entity).getHeldItemOffhand();
			SummonRecipe r = SummonRecipe.findRecipeWithKeepsake(stack, world, caster.iWantFoothold());
			if (r != null) {
				data.keepsake = stack.copy();
				data.summonRecipe = r;
				data.markContinue(true);
			}
		}
		if (data.isMarkContinue()) {
			if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative()) {
				int cost = data.summonRecipe.getSoulCost(data.keepsake, world, entity.getPosition());
				ItemStack stack = findSoulTool((EntityPlayer) entity, cost);
				if (stack.isEmpty()) data.markContinue(false);
			}
		}
	}

	public ItemStack findSoulTool(EntityPlayer player, int need) {
		InventoryPlayer inv = player.inventory;
		if (inv.currentItem - 1 >= 0) {
			ItemStack stack = inv.getStackInSlot(inv.currentItem - 1);
			int soul = ItemSoulWoodSword.getSoul(stack);
			if (soul >= need) return stack;
		}
		if (inv.currentItem + 1 <= 8) {
			ItemStack stack = inv.getStackInSlot(inv.currentItem + 1);
			int soul = ItemSoulWoodSword.getSoul(stack);
			if (soul >= need) return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void onCollectElement(World world, IMantraData mData, ICaster caster, int speedTick) {
		Data data = (Data) mData;
		// 收集能量
		if (!data.isMarkContinue()) return;
		if (data.power < 100) {
			ElementStack need = new ElementStack(ESInit.ELEMENTS.MAGIC, 1, 50);
			ElementStack get = caster.iWantSomeElement(need, true);
			if (get.isEmpty()) return;
			data.power++;
		}
		data.setProgress(data.power, 100);
	}

	@Override
	public void endSpelling(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		if (data.power < 100) return;
		WantedTargetResult wr = caster.iWantBlockTarget();
		data.pos = wr.getPos();
		if (data.pos == null) return;
		if (world.getBlockState(data.pos).getBlock() == ESInit.BLOCKS.RITE_TABLE) data.pos = data.pos.down();
		else data.pos = data.pos.up();
		if (data.summonRecipe != null)
			data.summon = data.summonRecipe.createSummon(data.keepsake, data.world, data.pos);
		// 重置位置
		ICasterObject co = caster.iWantDirectCaster();
		if (co.asEntity() != null) co.asEntity().setPosition(data.pos.getX(), data.pos.getY(), data.pos.getZ());
		// 消耗灵魂
		Entity entity = caster.iWantCaster().asEntity();
		if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative()) {
			int cost = data.summonRecipe.getSoulCost(data.keepsake, world, data.pos);
			ItemStack stack = findSoulTool((EntityPlayer) entity, cost);
			if (!stack.isEmpty()) ItemSoulWoodSword.addSoul(stack, -cost);
		}
	}

	@Override
	public boolean afterSpelling(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		if (data.isEmpty()) return false;
		if (world.isRemote) this.afterSpellingEffect(world, mData, caster);
		return data.summon.update();
	}

	@SideOnly(Side.CLIENT)
	public void afterSpellingEffect(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		if (data.hasMarkEffect(1000)) return;
		Entity entity = caster.iWantDirectCaster().asEntity();
		if (entity == null) return;
		EffectSummonRender ems = new EffectSummonRender(entity.world, data);
		ems.setCondition(new ConditionEffect(entity, data, 1000, false));
		data.addEffect(caster, ems, 1000);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		super.onSpellingEffect(world, mData, caster);
		this.addEffectIndicatorEffect(world, mData, caster);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColor(IMantraData mData) {
		if (mData == null) return super.getColor(mData);
		Data data = (Data) mData;
		if (data.summonRecipe == null) return super.getColor(mData);
		return data.summonRecipe.getColor(data.keepsake);
	}

}
