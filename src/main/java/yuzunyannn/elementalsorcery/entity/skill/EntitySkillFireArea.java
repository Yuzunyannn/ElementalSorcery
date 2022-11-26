package yuzunyannn.elementalsorcery.entity.skill;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class EntitySkillFireArea extends EntitySkillTarget {

	public EntitySkillFireArea(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 20);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		Vec3d vec = this.getTargetForecastVec(0).add(0, 0.2, 0);
		if (!checkAndDestroyBlock(vec)) return EntitySkill.SKILL_RESULT_FIN;
		BlockPos pos = new BlockPos(vec);
		while (pos.getY() > 0) {
			if (BlockHelper.isReplaceBlock(world, pos)) pos = pos.down();
			else break;
		}
		vec = new Vec3d(pos).add(0, 1.2, 0);
		this.fireArea(vec, true, 20 * 10,0, ESObjects.MANTRAS.FIRE_AREA, ESObjects.ELEMENTS.FIRE, 1000, 300);
		return EntitySkill.SKILL_RESULT_FIN;
	}

}
