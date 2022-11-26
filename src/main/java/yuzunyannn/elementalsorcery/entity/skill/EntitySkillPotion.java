package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.entity.EntityThrow;

public class EntitySkillPotion extends EntitySkillTarget {

	public static PotionType[] throwPotions = new PotionType[] { PotionTypes.POISON, PotionTypes.SLOWNESS,
			PotionTypes.WEAKNESS, ESObjects.POTION_TYPES.SILENT, PotionTypes.STRONG_LEAPING };

	public EntitySkillPotion(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 3);
	}

	@Override
	public int doSkill() {
		super.doSkill();

		Vec3d targetForecastVec = this.getTargetForecastVec(10);
		throwPotion(living, targetForecastVec, null);

		return EntitySkill.SKILL_RESULT_FIN;
	}

	public static void throwPotion(EntityLivingBase living, Vec3d target, PotionType pType) {
		if (pType == null) {
			Random rand = living.getRNG();
			pType = throwPotions[rand.nextInt(throwPotions.length)];
		}
		Vec3d pos = living.getPositionVector().add(0, 0.5, 0);
		Vec3d orient = target.subtract(pos);
		double length = orient.length();
		ItemStack potion = new ItemStack(Items.SPLASH_POTION);
		PotionUtils.addPotionToItemStack(potion, pType);
		EntityThrow.shoot(living, potion, EntityThrow.FLAG_POTION_BREAK).shoot(orient.x, orient.y, orient.z,
				(float) (Math.sqrt(length) / 4 + 0.5), 0);
	}
}
