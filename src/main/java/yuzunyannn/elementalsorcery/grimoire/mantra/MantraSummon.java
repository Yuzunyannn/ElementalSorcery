package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.ConditionEffect;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemSoulWoodSword;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectLookAt;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircle;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircleIcon;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectSummonRender;
import yuzunyannn.elementalsorcery.summon.Summon;
import yuzunyannn.elementalsorcery.summon.SummonRecipe;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraSummon extends MantraCommon {

	static public void summon(World world, BlockPos pos, EntityLivingBase summoner, ItemStack keepsake,
			SummonRecipe summonRecipe) {
		EntityGrimoire grimoire = new EntityGrimoire(world, summoner, instance, null,
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

	public final static MantraSummon instance = new MantraSummon();

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
		this.setRarity(2);
		this.setColor(0xda003e);
	}

	@Override
	public IMantraData getData(NBTTagCompound metaData, World world, ICaster caster) {
		return new Data(world);
	}

	@Override
	public void startSpelling(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		// 寻找召唤任务
		Entity entity = caster.iWantCaster();
		data.markContinue(false);
		if (entity instanceof EntityLivingBase) {
			ItemStack stack = ((EntityLivingBase) entity).getHeldItemOffhand();
			SummonRecipe r = SummonRecipe.findRecipeWithKeepsake(stack, world, caster.iWantFoothold());
			if (r != null) {
				data.keepsake = stack.copy();
				data.summonRecipe = r;
				data.markContinue(true);
			}
		}
		if (data.markContinue) {
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
	public void onSpelling(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		// 收集能量
		if (data.markContinue) {
			if (data.power < 100) {
				ElementStack need = new ElementStack(ESInit.ELEMENTS.MAGIC, 1, 50);
				ElementStack get = caster.iWantSomeElement(need, true);
				if (get.isEmpty()) return;
				Entity entity = caster.iWantCaster();
				if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) data.power += 25;
				else data.power++;
			}
		}
		super.onSpelling(world, data, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		if (data.power < 100) return;
		Entry<BlockPos, EnumFacing> entry = caster.iWantBlockTarget();
		if (entry == null) return;
		data.pos = entry.getKey().up();
		if (data.summonRecipe != null)
			data.summon = data.summonRecipe.createSummon(data.keepsake, data.world, data.pos);
		caster.iWantDirectCaster().setPosition(data.pos.getX(), data.pos.getY(), data.pos.getZ());
		Entity entity = caster.iWantCaster();
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
		Entity entity = caster.iWantDirectCaster();
		EffectSummonRender ems = new EffectSummonRender(entity.world, data);
		ems.setCondition(new ConditionEffect(entity, data, 1000, false));
		data.addEffect(caster, ems, 1000);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getProgressRate(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		return data.power / 100.0f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		super.onSpellingEffect(world, mData, caster);
		if (!caster.hasEffectFlags(MantraEffectFlags.INDICATOR)) return;
		Data data = (Data) mData;
		if (!data.markContinue) return;
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		if (caster.iWantCaster() == Minecraft.getMinecraft().player) if (!dataEffect.hasMarkEffect(1))
			dataEffect.addEffect(caster, new EffectLookAt(world, caster, this.getRenderColor(mData)), 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EffectMagicCircle getEffectMagicCircle(World world, EntityLivingBase entity, IMantraData mData) {
		EffectMagicCircle emc = new EffectMagicCircleIcon(world, entity, RenderObjects.MANTRA_SUMMON);
		emc.setColor(this.getRenderColor(mData));
		return emc;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(IMantraData mData) {
		if (mData == null) return super.getRenderColor(mData);
		Data data = (Data) mData;
		if (data.summonRecipe == null) return super.getRenderColor(mData);
		return data.summonRecipe.getColor(data.keepsake);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_SUMMON;
	}

}
