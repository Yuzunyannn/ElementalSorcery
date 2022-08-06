package yuzunyannn.elementalsorcery.potion;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class PotionWindShield extends PotionCommon {

	public PotionWindShield() {
		super(false, 0xaef8ff, "windShield");
		iconIndex = 10;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase owner, int amplifier) {
		if (owner.world.isRemote) addWindEffect(owner);

		World world = owner.world;
		Random rand = world.rand;
		float size = 0.5f + amplifier * 0.5f;
		AxisAlignedBB aabb = WorldHelper.createAABB(owner, owner.width + size, owner.height + size, size);

		float power = Math.min(0.5f + amplifier * 0.25f, 2);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb, e -> e instanceof IProjectile);
		Vec3d ownerAt = new Vec3d(owner.posX, owner.posY + owner.height / 2, owner.posZ);
		for (Entity entity : entities) {
			Vec3d tar = ownerAt.subtract(entity.getPositionVector());
			Vec3d change = new Vec3d(-tar.z, 0, tar.x).normalize();
			if (change.lengthSquared() < 0.1f)
				change = new Vec3d(rand.nextGaussian(), 0, rand.nextGaussian()).normalize();
			double dis = Math.max(1, tar.length());
			change = change.scale(power / dis);
			entity.motionX += change.x;
			entity.motionZ += change.z;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void addWindEffect(Entity entity) {
		Vec3d vec = entity.getPositionVector();
		float theta = entity.ticksExisted * 0.5f;
		float r = 1;
		float h = entity.height / 2 * 1.2f;
		Vec3d at = vec.add(r * MathHelper.sin(theta), h + h * MathHelper.sin(theta * 0.1f),
				r * MathHelper.cos(theta));
		EffectElementMove effect = new EffectElementMove(entity.world, at);
		effect.setColor(0xaef8ff);
		at = at.subtract(vec).normalize().scale(0.01f);
		effect.xDecay = effect.zDecay = 0.8;
		effect.xAccelerate = at.x;
		effect.zAccelerate = at.z;
		Effect.addEffect(effect);
	}

	public static void tryAttackEntityFrom(EntityLivingBase target, EntityLivingBase attacker, DamageSource source,
			float amount) {
		if (!target.isPotionActive(ESObjects.POTIONS.WIND_SHIELD)) return;
		int amplifier = target.getActivePotionEffect(ESObjects.POTIONS.WIND_SHIELD).getAmplifier();
		float power = Math.min(0.5f + amplifier * 0.25f, 2);

		Vec3d targetAt = new Vec3d(target.posX, target.posY + target.height / 2, target.posZ);
		Vec3d attackerAt = new Vec3d(attacker.posX, attacker.posY + attacker.height / 2, attacker.posZ);
		Vec3d tar = attackerAt.subtract(targetAt);
		double dis = Math.max(1, tar.length());
		tar = tar.add(0, 0.5, 0).normalize().scale(power / dis);

		attacker.motionX += tar.x;
		attacker.motionY += tar.y;
		attacker.motionZ += tar.z;
	}

}