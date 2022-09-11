package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemElementStone extends Item {

	public ItemElementStone() {
		this.setTranslationKey("elementStone");
		this.setMaxStackSize(1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new CapabilityProvider.ElementInventoryUseProvider(stack, new ElementInventory(4));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		IElementInventory inventory = ElementHelper.getElementInventory(stack);
		inventory.addInformation(worldIn, tooltip, flagIn);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		if (EntityHelper.checkSilent(player, SilentLevel.RELEASE))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(handIn));

		ItemStack stack = player.getHeldItem(handIn);
		RayTraceResult ray = WorldHelper.getLookAtBlock(world, player, 8);
		if (ray != null) {
			BlockPos pos = ray.getBlockPos();
			// 强制挖矿
			if (OreHelper.isOre(world.getBlockState(pos))
					&& this.consumeElement(ESObjects.ELEMENTS.METAL, stack, player, true)) {
				world.destroyBlock(pos, true);
				if (world.isRemote) this.showEffect(player, new ElementStack(ESObjects.ELEMENTS.METAL), pos);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}
		// 末影瞬移
		if (player.onGround) {

			BlockPos pos = player.getPosition();
			pos = pos.up(8);

			if (world.isAirBlock(pos) && this.consumeElement(ESObjects.ELEMENTS.ENDER, stack, player, true)) {

				if (world.isRemote) {
					for (int i = 0; i < 32; ++i) {
						world.spawnParticle(EnumParticleTypes.PORTAL, player.posX,
								player.posY + RandomHelper.rand.nextDouble() * 2.0D, player.posZ,
								RandomHelper.rand.nextGaussian(), 0.0D, RandomHelper.rand.nextGaussian());
					}
				}

				player.setPosition(pos.getX(), pos.getY(), pos.getZ());
				world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ENDERMEN_TELEPORT,
						SoundCategory.HOSTILE, 1.0F, 1.0F);
			}

		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);

	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
		EnumHand hand = ItemVortex.inEntityHand(entityIn, stack, itemSlot, isSelected);
		if (hand == null) return;
		EntityLivingBase entity = (EntityLivingBase) entityIn;

		if (ESAPI.silent.isSilent(entity, SilentLevel.RELEASE)) return;

		if (entity.isInWater()) {

			// 水中氧气
			if (entity.getAir() < 280 && this.consumeElement(ESObjects.ELEMENTS.WATER, stack, entity)) {
				entity.setAir(280);
			}

		} else if (entity.isBurning() || entity.isInLava()) {

			// 火中抗火
			if (this.consumeElement(ESObjects.ELEMENTS.FIRE, stack, entity)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20, 3));
			}

		} else {

			// 下落减速
			if (entity.motionY < -0.5 && this.consumeElement(ESObjects.ELEMENTS.AIR, stack, entity)) {
				entity.fallDistance *= 0.8;
				entity.motionY *= 0.8;
			}

			// 跑得快
			if (entity.onGround) {
				boolean hasCost = false;
				boolean needFast = Math.abs(entity.motionX) > 0.1 && Math.abs(entity.motionX) < 1.5;
				if (needFast && this.consumeElement(ESObjects.ELEMENTS.EARTH, stack, entity)) {
					entity.motionX *= 1.5;
					hasCost = true;
				}
				needFast = Math.abs(entity.motionZ) > 0.1 && Math.abs(entity.motionZ) < 1.5;
				if (needFast && (hasCost || this.consumeElement(ESObjects.ELEMENTS.EARTH, stack, entity))) {
					entity.motionZ *= 1.5;
				}
			}

		}

		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			// 食物
			FoodStats fs = player.getFoodStats();
			if (fs.needFood() && entity.ticksExisted % 10 == 0
					&& this.consumeElement(ESObjects.ELEMENTS.WOOD, stack, entity)) {
				fs.addStats(1, 0.5f);
			}
		}
	}

	public boolean consumeElement(Element ele, ItemStack stack, EntityLivingBase entity) {
		return this.consumeElement(ele, stack, entity, false);
	}

	public boolean consumeElement(Element ele, ItemStack stack, EntityLivingBase entity, boolean force) {
		ElementStack estack = new ElementStack(ele, 1, 10);
		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			if (entity.world.isRemote) this.showEffect(entity, estack);
			return true;
		}
		IElementInventory einv = ElementHelper.getElementInventory(stack);
		if (einv == null) return false;
		einv.loadState(stack);
		if (force || entity.ticksExisted % 40 == 0) {
			estack = einv.extractElement(estack, false);
			if (estack.isEmpty()) return false;
			einv.saveState(stack);
		} else {
			estack = einv.extractElement(estack, true);
			if (estack.isEmpty()) return false;
		}
		if (entity.world.isRemote) this.showEffect(entity, estack);
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void showEffect(EntityLivingBase entity, ElementStack element) {
		if (entity.ticksExisted % 4 != 0) return;
		Vec3d pos = entity.getPositionVector().add(Math.random() - 0.5, Math.random() * 2, Math.random() - 0.5);
		EffectElementMove em = new EffectElementMove(entity.world, pos);
		em.setColor(element.getColor());
		Effect.addEffect(em);
	}

	@SideOnly(Side.CLIENT)
	public void showEffect(EntityLivingBase entity, ElementStack element, BlockPos bPos) {
		for (int i = 0; i < 8; i++) {
			Vec3d pos = new Vec3d(bPos).add(Math.random(), Math.random(), Math.random());
			EffectElementMove em = new EffectElementMove(entity.world, pos);
			Vec3d v = new Vec3d(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize()
					.scale(Math.random() * 0.05);
			em.setVelocity(v);
			em.setColor(element.getColor());
			Effect.addEffect(em);
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}

}
