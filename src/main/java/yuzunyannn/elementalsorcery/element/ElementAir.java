package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.JuiceMaterial;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.target.WorldTarget;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.element.explosion.EEAir;
import yuzunyannn.elementalsorcery.element.explosion.EEOnSilent;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.var.Variables;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElementAir extends ElementCommon {

	public ElementAir() {
		super(0xe5ffff, "air");
		setTransition(2.5f, 202.5f, 120);
		setLaserCostOnce(1, 10);
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (tick % 5 != 0) return estack;
		int range = getStarFlowerRange(estack);
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, range, 0.5);
		List<EntityMob> entities = world.getEntitiesWithinAABB(EntityMob.class, aabb);
		for (EntityMob entity : entities) {
			Vec3d c = new Vec3d(pos).add(0.5, 0, 0.5);
			Vec3d tar = entity.getPositionVector().add(0, 0.5, 0).subtract(c);
//			double l = entity.getDistance(c.x, c.y, c.z) / 10;
			tar = tar.normalize().scale(1);// .scale(Math.min(1 / Math.max(0.01, l), 0.1));
			entity.addVelocity(tar.x, 0, tar.z);
		}
		if (entities.isEmpty()) return estack;
		estack.shrink(Math.max(1, entities.size() / 5));
		return estack;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		if (ESAPI.silent.isSilent(world, pos, SilentLevel.PHENOMENON))
			return new EEOnSilent(world, pos, ElementExplosion.getStrength(eStack), eStack);
		return new EEAir(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {

		helper.preparatory(MobEffects.SPEED, 32, 75);
		helper.check(JuiceMaterial.ELF_FRUIT, 65).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 10, 1);

		helper.preparatory(MobEffects.JUMP_BOOST, 32, 75);
		helper.check(JuiceMaterial.ELF_FRUIT, 65).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 10, 1);

		helper.preparatory(ESObjects.POTIONS.WIND_WALKER, 32, 85);
		helper.check(JuiceMaterial.APPLE, 85).checkRatio(JuiceMaterial.MELON, 0.5f, 1.5f).join();
		helper.descend(JuiceMaterial.MELON, 20, 0.8f);

		helper.preparatory(ESObjects.POTIONS.WIND_SHIELD, 32, 85);
		helper.check(JuiceMaterial.MELON, 85).checkRatio(JuiceMaterial.APPLE, 0.5f, 1.5f).join();
		helper.descend(JuiceMaterial.APPLE, 20, 0.8f);

	}

	@Override
	public void onLaserExecute(World world, IWorldObject caster, WorldTarget target, ElementStack lastCost,
			VariableSet content) {
		if (laserOnceCost.isEmpty()) return;
		if (!lastCost.isEmpty()) content.set(Variables.STORAGE_ELEMENT, lastCost);
		ElementStack storage = content.get(Variables.STORAGE_ELEMENT);
		if (storage.isEmpty()) return;
		int tick = content.get(Variables.TICK);
		int tickCount = (int) MathHelper.clamp(200 / MathHelper.sqrt(storage.getPower()), 3, 20);
		if (tick % tickCount == 0) storage.shrink(laserOnceCost.getCount());
		onExecuteLaser(world, caster, target, storage, content);
	}

	@Override
	protected void onExecuteLaser(World world, IWorldObject caster, WorldTarget target, ElementStack storage,
			VariableSet content) {
		if (world.isRemote) return;

		Vec3d casterVec = caster.getEyePosition();
		Entity entity = target.getEntity();
		float ratio = Math.max(MathHelper.sqrt(storage.getPower()) / 100f, 0.05f);
		EntityLivingBase casterEntity = caster.toEntityLiving();
		Vec3d tar = target.getHitVec().subtract(casterVec).normalize().scale(ratio);
		if (ratio > 0.5f) ratio = 0.5f;
		if (casterEntity != null && casterEntity.isSneaking()) {
			tar = tar.scale(-1);
			casterEntity.motionX += tar.x;
			casterEntity.motionY += tar.y * 1.1;
			casterEntity.motionZ += tar.z;
			if (tar.y > 0) casterEntity.fallDistance = 0;
			casterEntity.velocityChanged = true;
			return;
		}

		if (entity != null) {
			entity.motionX += tar.x;
			entity.motionY += tar.y * 1.1;
			entity.motionZ += tar.z;
			entity.velocityChanged = true;
			return;
		}

	}
}
