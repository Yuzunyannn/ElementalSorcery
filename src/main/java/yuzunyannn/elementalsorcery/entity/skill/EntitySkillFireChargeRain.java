package yuzunyannn.elementalsorcery.entity.skill;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class EntitySkillFireChargeRain extends EntitySkillTarget {

	protected List<EntityLivingBase> list;
	protected int index = 0;

	public EntitySkillFireChargeRain(EntityLivingBase entity) {
		super(entity);
		this.range = 64;
		this.setCD(20 * 16);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		this.index = 0;
		EntityLivingBase target = this.getAttackEntity();
		AxisAlignedBB aabb = WorldHelper.createAABB(target.getPosition(), 16, 5, 2);
		list = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, e -> {
			if (EntityHelper.isSameTeam(target, e)) return true;
			return target.getClass().isAssignableFrom(e.getClass());
		});
		if (list.isEmpty()) return EntitySkill.SKILL_RESULT_FIN;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

	@Override
	public int doContinueSkill() {
		if (list == null) return EntitySkill.SKILL_RESULT_FIN;
		if (list.isEmpty()) return EntitySkill.SKILL_RESULT_FIN;
		if (index >= list.size()) return EntitySkill.SKILL_RESULT_FIN;

		if (this.tick % 5 == 0) {
			Entity entity = list.get(index++);
			if (entity.isDead) return EntitySkill.SKILL_RESULT_CONTINUE;
			Vec3d pos = entity.getPositionVector().add(0, entity.height + 1, 0);
			if (!checkAndDestroyBlock(pos)) return EntitySkill.SKILL_RESULT_CONTINUE;
			Vec3d orient = new Vec3d(0, -1, 0);
			this.fireDirect(pos, orient, Vec3d.ZERO, 20 * 5, ESObjects.MANTRAS.FIRE_CHARGE, ESObjects.ELEMENTS.FIRE, 1000, 200);
		}

		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

}
