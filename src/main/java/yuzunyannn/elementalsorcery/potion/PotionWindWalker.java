package yuzunyannn.elementalsorcery.potion;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class PotionWindWalker extends PotionCommon {

	static double getEntitySpeed(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			double vx = player.chasingPosX - player.prevChasingPosX;
			double vz = player.chasingPosZ - player.prevChasingPosZ;
			return MathHelper.sqrt(vx * vx + vz * vz);
		} else return MathHelper.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
	}

	public PotionWindWalker() {
		super(false, 0xeafdff, "windWalker");
		iconIndex = 3;

		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "464b1825-1be7-4b0b-b90c-40e2d3e118d6",
				0.11, 2);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 5 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase owner, int amplifier) {
		double v = getEntitySpeed(owner);
		if (v < 0.1f) return;

		float range = Math.min(4, amplifier / 2 + 1);
		Vec3d center = owner.getPositionVector();
		AxisAlignedBB aabb = WorldHelper.createAABB(center, range, 2, 1);
		List<Entity> entities = owner.world.getEntitiesWithinAABB(Entity.class, aabb);

		if (owner.world.isRemote) addEffect(owner, v);

		if (entities.isEmpty()) return;

		for (Entity entity : entities) {
			if (entity == owner) continue;
			if (entity instanceof EntityItem || entity instanceof EntityLivingBase) {
				Vec3d tar = center.subtract(entity.getPositionVector()).normalize();
				Vec3d speed = tar.scale(v * (1 + 0.16f * amplifier));
				entity.motionX += speed.x;
				entity.motionY += speed.y;
				entity.motionZ += speed.z;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void addEffect(Entity owner, double v) {
		World world = owner.world;
		Vec3d vec = new Vec3d(owner.motionX, 0, owner.motionZ).normalize().scale(0.1f);
		{
			EffectElementMove move = new EffectElementMove(world, owner.getPositionVector());
			move.setColor(getLiquidColor());
			move.yAccelerate = 0.01f;
			Effect.addEffect(move);
		}
		if (v < 0.3) return;
		{
			EffectElementMove move = new EffectElementMove(world, owner.getPositionVector());
			move.motionX = -vec.z;
			move.motionZ = vec.x;
			move.setColor(getLiquidColor());
			move.yAccelerate = 0.01f;
			Effect.addEffect(move);
		}
		{
			EffectElementMove move = new EffectElementMove(world, owner.getPositionVector());
			move.motionX = vec.z;
			move.motionZ = -vec.x;
			move.setColor(getLiquidColor());
			move.yAccelerate = 0.01f;
			Effect.addEffect(move);
		}
	}
}
