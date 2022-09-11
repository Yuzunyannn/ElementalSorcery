package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;

public class ItemVortex extends Item implements EntityThrow.IItemThrowAction {

	public ItemVortex() {
		this.setTranslationKey("vortex");
	}

	private boolean absorb(World world, Vec3d center) {
		if (ESAPI.silent.isSilent(world, center, SilentLevel.PHENOMENON)) return false;
		int size = 12;
		AxisAlignedBB aabb = new AxisAlignedBB(center.x - size, center.y - size, center.z - size, center.x + size,
				center.y + size, center.z + size);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase entity : entities) {
			double dis = center.distanceTo(entity.getPositionVector());
			Vec3d tar = center.subtract(entity.getPositionVector()).normalize().scale(dis / 4);
			entity.addVelocity(tar.x, tar.y, tar.z);
		}
		if (!world.isRemote) {
			NBTTagCompound nbt = FireworkEffect.fastNBT(0, 2, 0.2f, new int[] { 0x005aff, 0x3ab7ff },
					new int[] { 0xb1f0ff });
			Effects.spawnEffect(world, Effects.FIREWROK, center, nbt);
		}
		return true;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.onGround) {
			World world = entityItem.world;
			Vec3d center = entityItem.getPositionVector().add(0.5, 0.5, 0.5);
			if (absorb(world, center)) entityItem.setDead();
		}
		return super.onEntityItemUpdate(entityItem);
	}

	static public EnumHand inEntityHand(Entity entityIn, ItemStack stack, int itemSlot, boolean isSelected) {
		if (!(entityIn instanceof EntityLivingBase)) return null;
		if (isSelected) return EnumHand.MAIN_HAND;
		EntityLivingBase entity = (EntityLivingBase) entityIn;
		if (itemSlot == 0 && stack == entity.getHeldItemOffhand()) return EnumHand.OFF_HAND;
		return null;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		EnumHand hand = ItemVortex.inEntityHand(entityIn, stack, itemSlot, isSelected);
		if (hand == null) return;
		if (ESAPI.silent.isSilent(entityIn, SilentLevel.PHENOMENON)) return;

		if (entityIn instanceof EntityPlayer) {
			if (((EntityPlayer) entityIn).isCreative()) return;
		}

		double f = 0.2;
		entityIn.motionX += (Math.random() - 0.5) * f;
		entityIn.motionY += (Math.random() - 0.5) * f;
		entityIn.motionZ += (Math.random() - 0.5) * f;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		EntityThrow.shoot(playerIn, playerIn.getHeldItem(handIn));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void onImpact(EntityThrow entity, RayTraceResult result) {
		Vec3d vec = result.hitVec;
		if (vec == null) return;
		if (result.entityHit != null) vec = vec.add(0, result.entityHit.height / 2, 0);
		if (!absorb(entity.world, vec)) entity.dropAsItem(result.hitVec);
	}
}
