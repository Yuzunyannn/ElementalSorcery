package yuzunyannn.elementalsorcery.entity.mob;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.math.AxisAlignedBB;
import yuzunyannn.elementalsorcery.api.entity.IHasMaster;

public class EntityAIMastersEnemyTarget<T extends EntityCreature & IHasMaster> extends EntityAITarget {

	protected IHasMaster binder;
	protected EntityLivingBase targetEntity;
	protected int targetChance;
	protected final EntityAINearestAttackableTarget.Sorter sorter;
	protected Predicate<EntityLivingBase> commontFilter;
	protected Predicate<EntityLivingBase> filterOwnerless = e -> {
		if (e == this.taskOwner) return false;
		if (commontFilter != null && !commontFilter.apply(e)) return false;
		if (e instanceof EntityCreature) {
			if (((EntityCreature) e).getAttackTarget() != null) return true;
		}
		if (e instanceof EntityLivingBase) {
			int time = e.ticksExisted - e.getLastAttackedEntityTime();
			return e.getLastAttackedEntity() != null && time < 20 * 30;
		}
		return false;
	};
	protected Predicate<EntityLivingBase> filterMasterEnemy = e -> {
		if (e == this.taskOwner) return false;
		if (commontFilter != null && !commontFilter.apply(e)) return false;
		EntityLivingBase master = binder.getMaster();
		if (e instanceof EntityCreature) {
			if (((EntityCreature) e).getAttackTarget() == master) return true;
		}
		if (e instanceof EntityLivingBase) {
			if (e.getAttackingEntity() == master) return true;
		}
		return false;
	};

	public EntityAIMastersEnemyTarget(T creature, int targetChance, Predicate<EntityLivingBase> filter) {
		super(creature, true);
		this.binder = creature;
		this.targetChance = targetChance;
		this.sorter = new EntityAINearestAttackableTarget.Sorter(creature);
		this.commontFilter = filter;
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.targetEntity);
		super.startExecuting();
	}

	@Override
	public boolean shouldExecute() {
		if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) return false;

		double distance = this.getTargetDistance();

		Entity target = this.taskOwner.getAttackTarget();
		if (target != null) next: {
			if (target.isDead) break next;
			if (this.taskOwner.getEntitySenses().canSee(target)) return false;
		}

		Predicate<EntityLivingBase> filter;

		if (binder.isOwnerless()) filter = filterOwnerless;
		else {
			EntityLivingBase master = binder.getMaster();
			if (master == null) return false;
			filter = filterMasterEnemy;
		}
		List<EntityLivingBase> list = this.taskOwner.world.getEntitiesWithinAABB(EntityLivingBase.class,
				this.getTargetableArea(distance), filter);
		if (list.isEmpty()) return false;
		Collections.sort(list, this.sorter);
		this.targetEntity = list.get(0);
		return true;
	}

	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
	}

}
