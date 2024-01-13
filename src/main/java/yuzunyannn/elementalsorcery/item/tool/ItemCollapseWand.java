package yuzunyannn.elementalsorcery.item.tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Multimap;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.logics.EventServer;
import yuzunyannn.elementalsorcery.logics.ITickTask;
import yuzunyannn.elementalsorcery.logics.IWorldTickTask;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementAbsorb;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.IItemUseClientUpdate;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemCollapseWand extends Item implements IItemUseClientUpdate {

	public ItemCollapseWand() {
		this.setTranslationKey("collapseWand");
		this.setMaxStackSize(1);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return TextFormatting.RED + super.getItemStackDisplayName(stack);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IElementInventory einv = ElementHelper.getElementInventory(stack);
		einv.addInformation(worldIn, tooltip, flagIn);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new CapabilityProvider.ElementInventoryUseProvider(stack, new ElementInventory(4));
	}

	@Override
	public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		return oldStack.getItem() == newStack.getItem();
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.5, 0));
		}
		return multimap;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity targetEntity) {
		float f = player.getCooledAttackStrength(0.5F);
		if (f != 1) return false;

		if (EntityHelper.checkSilent(player, SilentLevel.RELEASE)) return false;

		if (player.world.isRemote) return true;

		DamageSource ds = DamageSource.causeIndirectMagicDamage(player, player);
		Vec3d pos = targetEntity.getPositionVector().add(0, targetEntity.height / 2, 0);
		float dmg = 20;
		float size = 2.5f;
		AxisAlignedBB AABB = WorldHelper.createAABB(pos, size, size, size);
		List<Entity> entities = player.world.getEntitiesWithinAABB(targetEntity.getClass(), AABB);
		for (Entity entity : entities) {
			if (EntityHelper.isSameTeam(player, entity)) continue;
			Vec3d at = entity.getPositionVector().add(0, entity.height / 2, 0);
			float dmgRate = Math.min(1, 1 / (MathHelper.sqrt(pos.distanceTo(at) * 0.65f)));
			entity.attackEntityFrom(ds, dmgRate * dmg);
		}
		NBTTagCompound nbt = FireworkEffect.fastNBT(11, (int) size, 0.1f, new int[] { 0x4d21ff, 0xff2175 }, null);
		Effects.spawnEffect(player.world, Effects.FIREWROK, pos, nbt);
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (EntityHelper.checkSilent(playerIn, SilentLevel.RELEASE))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onUsingTickClient(ItemStack stack, EntityLivingBase player, int count) {
		count = this.getMaxItemUseDuration(stack) - count;
		count = Math.min(count, 80);
		Random rand = RandomHelper.rand;
		if (rand.nextInt(80) > count) return;

		Vec3d vec = player.getPositionVector().add(0, player.getEyeHeight(), 0);
		vec = vec.add(player.getLookVec()).add(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
		Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
		EffectElementMove effect = new EffectElementMove(player.world, vec);
		effect.setColor(0x4d21ff);
		if (count >= 80 && rand.nextFloat() < 0.5) effect.setColor(0xff2175);
		effect.setVelocity(speed.normalize().scale(0.025));
		Effect.addEffect(effect);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (worldIn.isRemote) return;

		int time = this.getMaxItemUseDuration(stack) - timeLeft;
		if (time < 20) return;
		time = Math.min(80, time);

		RayTraceResult rt = WorldHelper.getLookAtBlock(worldIn, entityLiving, 32);
		if (rt == null) return;

		BlockPos pos = rt.getBlockPos();
		TileElementReactor reactor = BlockHelper.getTileEntity(worldIn, pos, TileElementReactor.class);
		if (reactor != null && reactor.canOpenCrackStatus()) {
			reactor.setOpenCrackMark(true);
			worldIn.destroyBlock(pos, false);
			entityLiving.renderBrokenItemStack(stack);
			stack.shrink(1);
			if (entityLiving instanceof EntityPlayerMP)
				ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) entityLiving, "reactor:crack");
			return;
		}

		EventServer.addWorldTickTask(worldIn, new BlockDisintegrate(pos, 128 * time / 80, entityLiving));
	}

	public static class BlockDisintegrate implements IWorldTickTask {

		protected int prepareTick = 0;
		protected int count;
		protected BlockPos pos;
		protected Set<BlockPos> eMap;
		protected EntityLivingBase player;

		public BlockDisintegrate(BlockPos start, int count, EntityLivingBase player) {
			this(start, count, (BlockDisintegrate) null);
			this.eMap = new HashSet<>();
			this.player = player;
		}

		protected BlockDisintegrate(BlockPos start, int count, BlockDisintegrate other) {
			this.count = count;
			this.pos = start;
			this.prepareTick = RandomHelper.rand.nextInt(5) + 5;
			if (other == null) return;
			this.player = other.player;
			this.eMap = other.eMap;
		}

		protected void doDisintegrat(World world) {
			if (player == null) return;
			ItemStack wand = player.getHeldItem(EnumHand.MAIN_HAND);
			if (wand.getItem() != ESObjects.ITEMS.COLLAPSE_WAND) {
				wand = player.getHeldItem(EnumHand.OFF_HAND);
				if (wand.getItem() != ESObjects.ITEMS.COLLAPSE_WAND) wand = ItemStack.EMPTY;
			}
			if (BlockHelper.isBedrock(world, pos)) return;
			IBlockState state = world.getBlockState(pos);
			if (state.getMaterial() == Material.AIR) return;

			List<ItemStack> stacks = ItemHelper.getItemStackFromState(world, pos, state);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1);

			IElementInventory eInv = ElementHelper.getElementInventory(wand);
			boolean hasToInv = false;
			Iterator<ItemStack> iter = stacks.iterator();
			while (iter.hasNext()) {
				ItemStack stack = iter.next();
				IToElementInfo info = ElementMap.instance.toElement(stack);
				if (info == null) continue;
				iter.remove();
				if (eInv == null) continue;
				ElementStack[] eStacks = info.element();
				for (ElementStack sStack : eStacks) {
					sStack = sStack.copy();
					float r = Math.min(1, count / 128f);
					sStack.getElement().onDeconstructToElement(world, stack, sStack, info.complex(),
							(int) (Element.DP_ALTAR + (Element.DP_ALTAR_ADV - Element.DP_ALTAR) * r));
					eInv.insertElement(sStack, false);
				}
				hasToInv = true;
			}
			for (ItemStack stack : stacks) ItemHelper.dropItem(world, pos, stack);
			if (eInv != null) eInv.saveState(wand);
			spawnEffect(world, pos, hasToInv ? this.player : null);
		}

		protected boolean isSame(IBlockState my, IBlockState other) {
			if (my.getBlock() == Blocks.GRASS || my.getBlock() == Blocks.DIRT)
				return other.getBlock() == Blocks.DIRT || other.getBlock() == Blocks.GRASS;
			return my == other;
		}

		@Override
		public int onTick(World world) {
			if (prepareTick-- > 0) return ITickTask.SUCCESS;

			if (!world.isBlockLoaded(pos, false)) return ITickTask.END;
			if (world.isAirBlock(pos)) return ITickTask.END;
			IBlockState state = world.getBlockState(pos);
			doDisintegrat(world);
			if (--this.count <= 0) return ITickTask.END;

			List<BlockPos> nexts = new ArrayList<>();
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos at = pos.offset(facing);
				if (eMap.contains(at)) continue;
				if (!isSame(state, world.getBlockState(at))) continue;
				nexts.add(at);
			}
			if (nexts.isEmpty()) return ITickTask.END;
			int c = MathHelper.floor(this.count / nexts.size());
			int n = Math.min(this.count, nexts.size());
			for (int i = 0; i < n; i++) {
				this.eMap.add(nexts.get(i));
				EventServer.addWorldTickTask(world, new BlockDisintegrate(nexts.get(i), c, this));
			}

			return ITickTask.END;
		}

	}

	@SideOnly(Side.CLIENT)
	public static void show(World world, Vec3d pos, NBTTagCompound nbt) {
		Entity entity = null;
		if (nbt.hasKey("t")) entity = world.getEntityByID(nbt.getInteger("t"));
		BlockPos blockPos = new BlockPos(pos);
		IBlockState state = world.getBlockState(blockPos);
		List<ItemStack> stacks = ItemHelper.getItemStackFromState(world, blockPos, state);
		world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
		List<Integer> colors = new ArrayList<>();
		for (ItemStack stack : stacks) {
			ElementStack[] eStacks = ElementMap.instance.toElementStack(stack);
			if (eStacks == null) continue;
			for (ElementStack eStack : eStacks) colors.add(eStack.getColor());
		}
		if (colors.isEmpty()) {
			colors.add(0x4d21ff);
			colors.add(0xff2175);
		}
		Random rand = RandomHelper.rand;
		for (int i = 0; i < 4; i++) {
			if (entity != null) {
				EffectElementAbsorb effect = new EffectElementAbsorb(world, pos, entity);
				effect.setColor(colors.get(rand.nextInt(colors.size())));
				Effect.addEffect(effect);
			} else {
				EffectElementMove effect = new EffectElementMove(world, pos);
				Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
				effect.xDecay = effect.yDecay = effect.zDecay = 0.8;
				effect.setVelocity(speed.scale(0.1));
				effect.setColor(colors.get(rand.nextInt(colors.size())));
				Effect.addEffect(effect);
			}
		}
		Effect.mc.effectRenderer.addBlockDestroyEffects(blockPos, state);
	}

	public static void spawnEffect(World world, BlockPos at, Entity target) {
		NBTTagCompound nbt = new NBTTagCompound();
		if (target != null) nbt.setInteger("t", target.getEntityId());
		Effects.spawnEffect(world, Effects.COLLAPSE, new Vec3d(at).add(0.5, 0.5, 0.5), nbt);
	}
}
