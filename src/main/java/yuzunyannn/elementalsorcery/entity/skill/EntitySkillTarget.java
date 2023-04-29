package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra.AutoMantraConfig;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class EntitySkillTarget extends EntitySkill {

	protected EntityLivingBase living;
	protected float range = 32;

	public EntitySkillTarget(EntityLivingBase entity) {
		super(entity);
		this.living = entity;
	}

	public EntityLivingBase getAttackEntity() {
		if (this.living instanceof EntityCreature) {
			EntityLivingBase target = ((EntityCreature) this.living).getAttackTarget();
			if (target != null) return target;
		}
		return this.living.getAttackingEntity();
	}

	@Override
	public Random getRandom() {
		return living.getRNG();
	}

	public boolean checkHPLowerThan(double rate) {
//		if (ESAPI.isDevelop) return true;
		return (living.getHealth() / living.getMaxHealth()) <= rate;
	}

	@Override
	public boolean checkCanUse() {
		if (!super.checkCanUse()) return false;
		EntityLivingBase target = getAttackEntity();
		if (target == null) return false;
		if (target.getDistance(this.living) > this.range) return false;
		return true;
	}

	public boolean checkAndDestroyBlock(Vec3d vec) {
		BlockPos pos = new BlockPos(vec);
		if (BlockHelper.isBedrock(world, pos)) return false;
		world.destroyBlock(pos, true);
		return true;
	}

	public Vec3d getTargetForecastVec(int n) {
		EntityLivingBase target = this.getAttackEntity();
		if (target == null) return null;
		Vec3d speed = EntityHelper.getEntitySpeed(target);
		Vec3d targetForecastVec = target.getPositionEyes(0).add(speed.scale(n));
		return targetForecastVec;
	}

	public void fireDirect(Vec3d start, Vec3d orient, Vec3d move, int tick, Mantra mantra, Object... elements) {
		AutoMantraConfig config = new EntityAutoMantra.AutoMantraConfig();
		config.setMoveVec(move);
		EntityAutoMantra mantraEntity = new EntityAutoMantra(world, config, living, mantra, null);
		mantraEntity.setPosition(start.x, start.y, start.z);
		mantraEntity.setSpellingTick(tick);
		mantraEntity.setOrient(orient);
		IElementInventory elementInv = mantraEntity.getElementInventory();
		for (ElementStack eStack : ElementHelper.toArray(elements)) elementInv.insertElement(eStack, false);
		world.spawnEntity(mantraEntity);
	}

	public void fireArea(Vec3d start, boolean isRev, int tick, double potent, Mantra mantra, Object... elements) {
		AutoMantraConfig config = new EntityAutoMantra.AutoMantraConfig();
		config.setMoveVec(Vec3d.ZERO);
		if (isRev) config.blockTrack = AutoMantraConfig.BLOCKTRACK_DIRECT_REVERSE;
		EntityAutoMantra mantraEntity = new EntityAutoMantra(world, config, living, mantra, null);
		mantraEntity.setPosition(start.x, start.y, start.z);
		mantraEntity.setSpellingTick(tick);
		mantraEntity.setOrient(new Vec3d(0, isRev ? 1 : -1, 0));
		if (potent > 0) mantraEntity.iWantGivePotent(100, (float) potent);
		IElementInventory elementInv = mantraEntity.getElementInventory();
		for (ElementStack eStack : ElementHelper.toArray(elements)) elementInv.insertElement(eStack, false);
		world.spawnEntity(mantraEntity);
	}

	public void fireTrace(Vec3d start, Vec3d orient, EntityLivingBase target, double move, int tick, double potent,
			Mantra mantra, Object... elements) {
		AutoMantraConfig config = new EntityAutoMantra.AutoMantraConfig();
		config.setTarget(target, move);
		EntityAutoMantra mantraEntity = new EntityAutoMantra(world, config, living, mantra, null);
		mantraEntity.setPosition(start.x, start.y, start.z);
		mantraEntity.setSpellingTick(tick);
		mantraEntity.setOrient(orient);
		if (potent > 0) mantraEntity.iWantGivePotent(100, (float) potent);
		IElementInventory elementInv = mantraEntity.getElementInventory();
		for (ElementStack eStack : ElementHelper.toArray(elements)) elementInv.insertElement(eStack, false);
		world.spawnEntity(mantraEntity);
	}

}
