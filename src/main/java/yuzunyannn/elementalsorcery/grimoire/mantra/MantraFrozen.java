package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.math.Line3d;
import yuzunyannn.elementalsorcery.util.math.Plane3d;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class MantraFrozen extends MantraTypePersistent {

	final public static float SUPER_POTENT_POWER = 0.5f;

	public MantraFrozen() {
		this.setTranslationKey("frozen");
		this.setColor(0x66b8e7);
		this.setIcon("frozen");
		this.setRarity(60);
		this.setOccupation(3);
		this.setInterval(5);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.WATER, 1, 30), true);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.AIR, 1, 5), true);
		this.setDirectLaunchFragmentMantraLauncher(
				ElementHelper.toList(new ElementStack(ESObjects.ELEMENTS.WATER, 60, 60),
						new ElementStack(ESObjects.ELEMENTS.AIR, 60, 160)),
				2.5, 0.002, null);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		float potent = caster.iWantBePotent(0.2f, false);
		if (target instanceof EntityLivingBase)
			growFrozen((EntityLivingBase) target, (int) (80 * (1 + potent)), potent >= SUPER_POTENT_POWER ? 8 : 3);
	}

	@Override
	protected void onUpdate(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;

		int waterPower = mData.get(ESObjects.ELEMENTS.WATER).getPower();
		int airPower = mData.get(ESObjects.ELEMENTS.AIR).getPower();

		Vec3d dir = caster.iWantDirection().normalize();
		Vec3d vec = caster.iWantCaster().getEyePosition();
		int frozenTime = MathHelper.ceil((waterPower / 50.0f) * 20 + 80);
		int frozenSize = Math.min(airPower / 75 + 1, 8) + 1;
		boolean isSuperFrozen = false;
		if (caster.iWantBePotent(0.0075f, true) >= SUPER_POTENT_POWER) {
			caster.iWantBePotent(0.0075f, false);
			isSuperFrozen = true;
			frozenSize *= 1.5f;
		}

		if (world.isRemote) {
			playEffect(world, vec, dir, frozenSize, isSuperFrozen);
			return;
		}

		updateOnce(world, caster, vec, dir, frozenTime, frozenSize, isSuperFrozen);
	}

	@Override
	public boolean afterSpelling(World world, IMantraData data, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		MantraDataCommon mData = (MantraDataCommon) data;

		ElementStack eStackWater = mData.get(ESObjects.ELEMENTS.WATER);
		ElementStack eStackAir = mData.get(ESObjects.ELEMENTS.AIR);

		if (tick % 5 == 0) {
			eStackWater.shrink(1);
			eStackAir.shrink(1);
		}

		if (eStackWater.isEmpty() || eStackAir.isEmpty()) return false;

		Vec3d dir = caster.iWantDirection().normalize();
		Vec3d vec = caster.iWantCaster().getEyePosition().add(0, 0.5, 0);
		int frozenTime = MathHelper.ceil((eStackWater.getPower() / 50.0f) * 20 + 80);
		int frozenSize = Math.min(eStackAir.getPower() / 75 + 1, 8) + 1;
		boolean isSuperFrozen = true;

		if (world.isRemote) {
			playEffect(world, vec, dir, frozenSize, isSuperFrozen);
			return true;
		}

		updateOnce(world, caster, vec, dir, frozenTime, frozenSize, isSuperFrozen);

		return true;
	}

	protected BlockPos findSnowPos(World world, ICaster caster, Vec3d vec, int frozenSize, boolean isSuperFrozen) {
		Random rand = world.rand;
		if (isSuperFrozen) {
			BlockPos selectPos = new BlockPos(
					vec.add(rand.nextGaussian() * frozenSize, 0, rand.nextGaussian() * frozenSize));
			for (int i = 0; i < 8; i++) {
				BlockPos check = selectPos.add(0, i % 2 == 0 ? (i / 2 + 1) : (-i / 2 - 1), 0);
				if (BlockHelper.isSolidBlock(world, check) && BlockHelper.isReplaceBlock(world, check.up()))
					return check;
			}
		} else {
			BlockPos snowCoverPos = caster.iWantBlockTarget().getPos();
			if (snowCoverPos == null) return null;
			double dis = vec.distanceTo(new Vec3d(snowCoverPos));
			if (dis / (frozenSize * 1.05) > rand.nextFloat()) return null;
			if (!BlockHelper.isSolidBlock(world, snowCoverPos) || !BlockHelper.isReplaceBlock(world, snowCoverPos.up()))
				return null;
			return snowCoverPos;
		}
		return null;
	}

	protected void updateOnce(World world, ICaster caster, Vec3d vec, Vec3d dir, int frozenTime, int frozenSize,
			boolean isSuperFrozen) {
		int tick = caster.iWantKnowCastTick();
		float checkSize = frozenSize + 0.25f;

		BlockPos snowCoverPos = findSnowPos(world, caster, vec, frozenSize, isSuperFrozen);
		if (snowCoverPos != null) {
			snowCoverPos = snowCoverPos.up();
			if (BlockHelper.isFluid(world, snowCoverPos)) {

			} else world.setBlockState(snowCoverPos, Blocks.SNOW_LAYER.getDefaultState());
		}

		AxisAlignedBB nAABB = WorldHelper.createAABB(vec.add(dir.scale(0.5)), checkSize / 2, checkSize / 2,
				checkSize / 2);
		AxisAlignedBB sAABB = WorldHelper.createAABB(vec, checkSize, checkSize, checkSize);
		AxisAlignedBB useAABB = isSuperFrozen ? nAABB : sAABB;

		List<EntityLivingBase> livings = world.getEntitiesWithinAABB(EntityLivingBase.class, useAABB);
		int maxFrozenLevel = isSuperFrozen ? 8 : 3;

		Line3d line = Line3d.ofPointDirection(vec, dir);
		for (EntityLivingBase entity : livings) {
			if (isCasterFriend(caster, entity)) continue;
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
			if (!entity.onGround) speedUp = speedUp.scale(0.2);
			entity.motionX += speedUp.x;
			entity.motionY += speedUp.y / 2;
			entity.motionZ += speedUp.z;
			entity.velocityChanged = true;

			if (tick % 20 == 0) growFrozen(entity, (int) (frozenTime * sqPower), maxFrozenLevel);
		}
	}

	static public void growFrozen(EntityLivingBase entity, int frozenTime, int maxFrozenLevel) {
		BlockPos pos = entity.getPosition();
		float temperature = entity.world.getBiome(pos).getTemperature(pos);
		if (temperature < 0.25f) maxFrozenLevel++;
		else if (temperature > 1.75f) maxFrozenLevel--;
		if (temperature < 0.75f) frozenTime = (int) (frozenTime * (1 + 1 - temperature / 0.75f));
		else if (temperature > 1.25f) frozenTime = (int) (frozenTime / Math.min(2, temperature - 0.25f));

		PotionEffect effect = entity.getActivePotionEffect(ESObjects.POTIONS.FROZEN);
		int amplifier = effect == null ? 0 : effect.getAmplifier() + 1;
		int duration = effect == null ? 0 : effect.getDuration();
		duration = duration + frozenTime;
		if (amplifier < maxFrozenLevel) {
			float needTime = (float) (Math.pow(amplifier, 1.75) * 20 * 60);
			if (duration > needTime) {
				duration = duration / 2;
				amplifier++;
			}
		}
		entity.addPotionEffect(new PotionEffect(ESObjects.POTIONS.FROZEN, duration, amplifier - 1));
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
