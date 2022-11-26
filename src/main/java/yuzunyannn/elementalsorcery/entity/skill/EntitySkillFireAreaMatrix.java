package yuzunyannn.elementalsorcery.entity.skill;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class EntitySkillFireAreaMatrix extends EntitySkillTarget {

	public EntitySkillFireAreaMatrix(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 90);
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.5);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		Vec3d vec = this.getTargetForecastVec(0).add(0, 10, 0);

		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {

				BlockPos pos = new BlockPos(vec.add(dx * 14, 0, dz * 14));
				while (pos.getY() > 0) {
					if (BlockHelper.isReplaceBlock(world, pos)) pos = pos.down();
					else break;
				}

				Vec3d at = new Vec3d(pos).add(0, 1.2, 0);
				this.fireArea(at, true, 20 * 10,0, ESObjects.MANTRAS.FIRE_AREA, ESObjects.ELEMENTS.FIRE, 1000, 500);

			}
		}

		return EntitySkill.SKILL_RESULT_FIN;
	}

}
