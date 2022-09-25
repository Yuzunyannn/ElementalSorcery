package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectSnow;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.math.Line3d;
import yuzunyannn.elementalsorcery.util.math.Plane3d;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class MantraFrozen extends MantraCommon {

	public MantraFrozen() {
		this.setTranslationKey("frozen");
		this.setColor(0x66b8e7);
		this.setIcon("frozen");
		this.setRarity(75);
		this.setOccupation(1);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		MantraDataCommon mData = (MantraDataCommon) data;

		if (tick % 5 == 0 || !mData.isMarkContinue()) {
			ElementStack needWater = new ElementStack(ESObjects.ELEMENTS.WATER, 1, 30);
			ElementStack getWater = caster.iWantSomeElement(needWater, false);
			ElementStack needAir = new ElementStack(ESObjects.ELEMENTS.AIR, 1, 3);
			ElementStack getAir = caster.iWantSomeElement(needAir, false);
			if (getWater.isEmpty() || getAir.isEmpty()) mData.markContinue(false);
			else {
				mData.markContinue(true);
				caster.iWantSomeElement(needWater, true);
				caster.iWantSomeElement(needAir, true);
				mData.remove(ESObjects.ELEMENTS.WATER);
				mData.remove(ESObjects.ELEMENTS.AIR);
				mData.add(getWater);
				mData.add(getAir);
			}
		}

		if (!mData.isMarkContinue()) return;

		int waterPower = mData.get(ESObjects.ELEMENTS.WATER).getPower();
		int airPower = mData.get(ESObjects.ELEMENTS.AIR).getPower();

		Vec3d dir = caster.iWantDirection().normalize();
		Vec3d vec = caster.iWantCaster().getEyePosition();
		int frozenTime = MathHelper.ceil((waterPower / 50.0f) * 20 + 80);
		int frozenSize = Math.min(airPower / 75 + 1, 8) + 1;
		boolean isSuperFrozen = false;
		if (caster.iWantBePotent(0.005f, true) > 0.5f) {
			caster.iWantBePotent(0.005f, false);
			isSuperFrozen = true;
			frozenSize *= 1.5f;
		}

		if (world.isRemote) {
			this.onSpellingEffect(world, mData, caster);
			playEffect(world, vec, dir, frozenSize, isSuperFrozen);
			return;
		}

		float checkSize = frozenSize + 0.25f;
		AxisAlignedBB nAABB = WorldHelper.createAABB(vec.add(dir.scale(0.5)), checkSize / 2, checkSize / 2,
				checkSize / 2);
		AxisAlignedBB sAABB = WorldHelper.createAABB(vec, checkSize, checkSize, checkSize);
		AxisAlignedBB useAABB = isSuperFrozen ? nAABB : sAABB;

		Entity entityCaster = caster.iWantCaster().asEntity();
		List<EntityLivingBase> livings = world.getEntitiesWithinAABB(EntityLivingBase.class, useAABB);

		int maxFrozenLevel = isSuperFrozen ? 8 : 3;

		Line3d line = Line3d.ofPointDirection(vec, dir);
		for (EntityLivingBase entity : livings) {
			if (EntityHelper.isSameTeam(entityCaster, entity)) continue;
			double power = 0;
			Vec3d at = entity.getPositionVector().add(0, entity.height / 2, 0);
			double dis = line.lengthOfPoionToLine(at);
			if (isSuperFrozen) {
				double dns = dis / (frozenSize / 4 * 3);
				power = (dns < 1 ? (1 - dns * 0.6) : (0.4 / (dns * dns)));
			} else {
				Vec3d c2e = at.subtract(vec);
				double x = c2e.length();
				double n = MathHelper.sqrt(x * x - dis * dis);
				double cos = line.includedAngle(Line3d.ofPointDirection(at, c2e));
				power = (dis < 1 ? (1 - dis * 0.6) : (0.4 / (dis * dis))) * cos;
				if (n > frozenSize / 4 * 3) power = power / (n - frozenSize / 4 * 3 + 1);
			}
			if (power < 0.1) continue;
			float sqPower = MathHelper.sqrt(power);
			PotionEffect effect = entity.getActivePotionEffect(ESObjects.POTIONS.FROZEN);
			int amplifier = effect == null ? 0 : effect.getAmplifier() + 1;
			Vec3d speedUp = dir.scale(sqPower).scale(0.075 + Math.min(0.025 * amplifier, 0.1));
			if (!entity.onGround) speedUp = speedUp.scale(0.25);
			entity.motionX += speedUp.x;
			entity.motionY += speedUp.y / 2;
			entity.motionZ += speedUp.z;
			entity.velocityChanged = true;

			if (tick % 20 == 0) {
				int newFrozenTime = (int) (frozenTime * sqPower);
				int duration = effect == null ? 0 : effect.getDuration();
				duration = duration + newFrozenTime;
				if (amplifier < maxFrozenLevel) {
					float needTime = (float) (Math.pow(amplifier, 1.75) * 20 * 60);
					if (duration > needTime) {
						duration = duration / 2;
						amplifier++;
					}
				}
				entity.addPotionEffect(new PotionEffect(ESObjects.POTIONS.FROZEN, duration, amplifier - 1));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	static public void playEffect(World world, Vec3d vec, Vec3d dir, float frozenSize, boolean isSuper) {
		Random rand = world.rand;
		Vec3d nDir = dir.normalize();
		vec = vec.add(nDir.scale(0.5));
		int times = (int) MathHelper.clamp(frozenSize / 2, 1, 8);
		if (isSuper) {
			vec = vec.add(nDir.scale(-frozenSize));
			Plane3d plane = Plane3d.ofPointNormal(vec, nDir);
			Vec3d mv = Math.abs(nDir.y) == 1 ? new Vec3d(1, 0, 0) : new Vec3d(0, 1, 0);
			Vec3d v1 = plane.getProjection(mv).normalize();
			Vec3d v2 = Plane3d.ofTwoVec(v1, nDir).getNormal().normalize();
			for (int i = 0; i < times * times * times; i++) {
				double rv1 = (rand.nextDouble() * 2 - 1) * frozenSize;
				double rv2 = (rand.nextDouble() * 2 - 1) * frozenSize;
				Vec3d at = vec.add(v1.scale(rv1)).add(v2.scale(rv2));
				EffectSnow effect = new EffectSnow(world, at);
				effect.scale *= 2;
				float v = frozenSize * 2 / (float) effect.lifeTime;
				Vec3d deviation = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
				Vec3d acce = nDir.add(deviation.scale(0.1)).normalize();
				effect.setAccelerate(acce.scale(v * 0.1));
				effect.setVelocity(nDir.scale(v * 2));
				effect.setDecay(0.9);
				Effect.addEffect(effect);
			}
		} else {
			for (int i = 0; i < times; i++) {
				EffectSnow effect = new EffectSnow(world, vec);
				float v = frozenSize / (float) effect.lifeTime;
				Vec3d deviation = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
				Vec3d acce = nDir.add(deviation.scale(0.2)).normalize();
				effect.setAccelerate(acce.scale(v * 0.2));
				effect.setDecay(0.8);
				Effect.addEffect(effect);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		super.onSpellingEffect(world, mData, caster);
		this.addEffectEmitEffect(world, mData, caster);
	}

}
