package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.FireworkEffect;

public class ItemVortex extends Item {

	public ItemVortex() {
		this.setUnlocalizedName("vortex");
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.onGround) {
			World world = entityItem.world;
			entityItem.setDead();
			if (entityItem.world.isRemote) {
				NBTTagCompound nbt = FireworkEffect.fastNBT(0, 2, 0.2f, new int[] { 0x005aff, 0x3ab7ff },
						new int[] { 0xb1f0ff });
				Effects.spawnEffect(world, Effects.FIREWROK, entityItem.getPositionVector(), nbt);
			}
			int size = 8;
			Vec3d center = entityItem.getPositionVector().addVector(0.5, 0.5, 0.5);
			AxisAlignedBB aabb = new AxisAlignedBB(center.x - size, center.y - size, center.z - size, center.x + size,
					center.y + size, center.z + size);
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
			for (EntityLivingBase entity : entities) {
				double dis = center.distanceTo(entity.getPositionVector());
				Vec3d tar = center.subtract(entity.getPositionVector()).normalize().scale(dis / 4);
				entity.addVelocity(tar.x, tar.y, tar.z);
			}
		}
		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!isSelected && itemSlot != 0) return;
		double f = 0.2;
		entityIn.motionX += (Math.random() - 0.5) * f;
		entityIn.motionY += (Math.random() - 0.5) * f;
		entityIn.motionZ += (Math.random() - 0.5) * f;
	}
}
