package yuzunyannn.elementalsorcery.entity.skill;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class EntitySkillIceTrapMatrix extends EntitySkillTarget {

	public EntitySkillIceTrapMatrix(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 80);
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.5);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		Vec3d vec = this.getTargetForecastVec(0).add(0, 6, 0);

		for (int dx = -2; dx <= 2; dx++) {
			for (int dz = -2; dz <= 2; dz++) {
				BlockPos pos = new BlockPos(vec.add(dx * 7, 0, dz * 7));
				Vec3d at = new Vec3d(pos);
				this.fireArea(at, false, 20 * 2, 0.75, ESObjects.MANTRAS.ICE_CRYSTAL_BOMB, ESObjects.ELEMENTS.WATER,
						1000, 550);
				while (pos.getY() > 0) {
					if (BlockHelper.isReplaceBlock(world, pos)) pos = pos.down();
					else break;
				}
				at = new Vec3d(pos).add(0.5, 1.1, 0.5);
				this.fireDirect(at, new Vec3d(0, 1, 0), Vec3d.ZERO, 20 * 30, ESObjects.MANTRAS.FROZEN,
						ESObjects.ELEMENTS.WATER, 1000, 750, ESObjects.ELEMENTS.AIR, 1000, 500);
			}
		}

		return EntitySkill.SKILL_RESULT_FIN;
	}

}
