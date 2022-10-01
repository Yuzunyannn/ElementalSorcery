package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.mantra.CastStatus;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.ICasterObject;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.MantraEffectType;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.CollectResult;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectMap;
import yuzunyannn.elementalsorcery.grimoire.remote.FMantraIceCrystalBomb;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectIceCrystalBomb;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class MantraIceCrystalBomb extends MantraCommon {

	public static final Variable<Integer> TRIGGER_TICK = new Variable<>("triggerTick", VariableSet.INT);

	public MantraIceCrystalBomb() {
		this.setTranslationKey("iceCrystalBomb");
		this.setColor(0xa2c0f4);
		this.setIcon("ice_crystal_bomb");
		this.setRarity(50);
		this.addFragmentMantraLauncher(new FMantraIceCrystalBomb());
//		this.setDirectLaunchFragmentMantraLauncher(new ElementStack(ESObjects.ELEMENTS.WOOD, 125, 50), 2, 0.0075, null);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {

		float potent = caster.iWantBePotent(0.4f, true);
		if (potent < 0.25) {
			super.potentAttack(world, grimoire, caster, target);
			return;
		}

		doPotentAttackEffect(world, caster, target);

		ElementStack eStack = caster.iWantSomeElement(new ElementStack(ESObjects.ELEMENTS.WATER, 10, 25), true);
		if (eStack.isEmpty()) {
			super.potentAttack(world, grimoire, caster, target);
			return;
		}

		EntityLivingBase player = caster.iWantCaster().asEntityLivingBase();
		float damage = DamageHelper.getNormalAttackDamage(player, target);

		potent = caster.iWantBePotent(0.4f, false);
		bombEntity(target, caster, ElementTransition.toFragment(eStack) * (1 + potent / 4), damage);
		if (world.isRemote) EffectIceCrystalBomb.playEndBlastEffect(world,
				target.getPositionVector().add(0, target.height / 2, 0), true);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		((MantraDataCommon) data).markContinue(true);
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		MantraDataCommon mData = (MantraDataCommon) data;
		CollectResult cr = mData.tryCollect(caster, ESObjects.ELEMENTS.WATER, 5, 50, 100);
		mData.setProgress(cr.getStackCount(), 100);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		WorldTarget wt = caster.iWantBlockTarget();
		BlockPos pos = wt.getPos();
		MantraDataCommon mData = (MantraDataCommon) data;
		if (pos == null) {
			mData.remove(ESObjects.ELEMENTS.WATER);
			return;
		}
		mData.set(POWERF, caster.iWantBePotent(0.5f, false));
		caster.iWantDirectCaster().setPositionVector(new Vec3d(pos.offset(wt.getFace())).add(0.5, 0.5, 0.5));
	}

	@Override
	public boolean afterSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;

		ElementStack water = mData.get(ESObjects.ELEMENTS.WATER);
		if (water.isEmpty()) return false;

		if (world.isRemote) {
			afterSpellingEffect(world, data, caster);
			return true;
		}

		boolean isSuper = mData.get(POWERF) > 0.25f;

		if (mData.has(TRIGGER_TICK)) {
			int triggerTick = mData.get(TRIGGER_TICK);
			mData.set(TRIGGER_TICK, triggerTick - 1);
			if (triggerTick <= 0) {
				onBomb(world, mData, caster, caster.iWantDirectCaster().getPositionVector());
				return false;
			}
			return true;
		}

		int tick = mData.get(TICK);
		mData.set(TICK, tick + 1);

		if (tick > water.getCount() * 12 * 3) {
			triggerBomb(world, mData, caster);
			return true;
		}

		if (tick % 5 == 0) {
			Vec3d vec = caster.iWantDirectCaster().getPositionVector();
			double size = isSuper ? 4 : 1;

			AxisAlignedBB nAABB = WorldHelper.createAABB(vec, size, size, size);
			AxisAlignedBB sAABB = WorldHelper.createAABB(vec, size, size, size);
			AxisAlignedBB useAABB = isSuper ? nAABB : sAABB;

			AxisAlignedBB myBox = WorldHelper.createAABB(vec, 0.5, 0.5, 0.5);

			List<EntityLivingBase> livings = world.getEntitiesWithinAABB(EntityLivingBase.class, useAABB);
			for (EntityLivingBase entity : livings) {
				if (isCasterFriend(caster, entity)) continue;
				if (entity.getEntityBoundingBox().intersects(myBox)) {
					triggerBomb(world, mData, caster);
					return true;
				}
				if (!isSuper) continue;
				double distance = entity.getDistance(vec.x, vec.y, vec.z);
				double power = distance / size;
				power = 1 - (power * power);
				if (power <= 0) continue;
				Vec3d absorb = vec.subtract(entity.getPositionVector().add(0, entity.height / 2, 0));
				absorb = absorb.normalize().scale(0.3 * power * world.rand.nextDouble());
				entity.motionX += absorb.x;
				entity.motionY += absorb.y;
				entity.motionZ += absorb.z;
				entity.velocityChanged = true;
			}
		}

		return true;
	}

	public void triggerBomb(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		if (mData.has(TRIGGER_TICK)) return;
		mData.set(TRIGGER_TICK, 10);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("bomb", mData.get(TRIGGER_TICK).byteValue());
		caster.sendToClient(nbt);
	}

	protected void onBomb(World world, IMantraData data, ICaster caster, Vec3d vec) {
		MantraDataCommon mData = (MantraDataCommon) data;
		ElementStack water = mData.get(ESObjects.ELEMENTS.WATER);
		mData.remove(ESObjects.ELEMENTS.WATER);

		float potent = mData.get(POWERF);
		float size = 2.25f;
		AxisAlignedBB AABB = WorldHelper.createAABB(vec, size, size, size);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AABB);
		for (Entity entity : entities) {
			if (isCasterFriend(caster, entity)) continue;
			double distance = entity.getDistance(vec.x, vec.y, vec.z);
			double power = distance / size;
			power = 1 - (power * power);
			if (power < 0) continue;
			double powerCount = ElementTransition.toFragment(water) * power * (1 + potent / 2);
			bombEntity(entity, caster, powerCount,
					(float) Math.min(6, MathHelper.sqrt(water.getPower()) / 5f * power) * (1 + potent / 2));
		}
	}

	protected void bombEntity(Entity target, ICaster caster, double powerCount, float moreDMG) {
		if (target.world.isRemote) return;
		if (target instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) target;
			float r = (float) (powerCount / 750000);
			PotionEffect effect = living.getActivePotionEffect(ESObjects.POTIONS.FROZEN);
			int amplifier = effect == null ? 0 : effect.getAmplifier() + 1;
			int duration = effect == null ? 0 : effect.getDuration();
			DamageSource ds = caster.iWantDamageSource(ESObjects.ELEMENTS.WATER);
			float dmg = Math.min(4, r * 2 + 1) * (1 + Math.min(3, duration / (20 * 60 * 5)));
			target.attackEntityFrom(ds, (float) (dmg * Math.pow(amplifier, 1.5)) + moreDMG);
			MantraFrozen.growFrozen(living, (int) Math.min(40 + duration * (1 + r / 2), Short.MAX_VALUE), 1);
		} else if (target instanceof ICaster) {
			if (target == caster) return;
			ICaster otherCaster = (ICaster) target;
			if (otherCaster.iWantMantra() == this) {
				MantraDataCommon mData = (MantraDataCommon) otherCaster.iWantMantraData();
				if (mData != null) this.triggerBomb(target.world, mData, otherCaster);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		super.onSpellingEffect(world, data, caster);
		addEffectIndicatorEffect(world, data, caster);
	}

	@SideOnly(Side.CLIENT)
	public void afterSpellingEffect(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		if (mData.getEffectMap().hasMark(MantraEffectType.MANTRA_EFFECT_1)) return;
		ICasterObject casterObject = caster.iWantDirectCaster();
		EffectIceCrystalBomb ems = new EffectIceCrystalBomb(casterObject.getWorld(), casterObject);
		ems.setCondition(MantraEffectMap.condition(caster, mData, CastStatus.AFTER_SPELLING));
		mData.getEffectMap().addAndMark(MantraEffectType.MANTRA_EFFECT_1, ems);
		if (caster.iWantCaster().asPlayer() != null) ems.passHidePlayer = caster.iWantCaster().asPlayer().getUniqueID();
		ems.isSuper = mData.get(POWERF) > 0.25f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void recvData(World world, IMantraData data, ICaster caster, NBTTagCompound recvData) {
		if (recvData.hasKey("bomb")) {
			afterSpellingEffect(world, data, caster);
			MantraDataCommon mData = (MantraDataCommon) data;
			EffectIceCrystalBomb effect = mData.getEffectMap().getMark(MantraEffectType.MANTRA_EFFECT_1,
					EffectIceCrystalBomb.class);
			if (effect != null) effect.toBomb(recvData.getInteger("bomb"));
		} else super.recvData(world, data, caster, recvData);
	}

}
