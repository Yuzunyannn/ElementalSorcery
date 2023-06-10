package yuzunyannn.elementalsorcery.entity.skill;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

/** 不死族拿起瞬间伤害药水，进行使用 */
public class EntitySkilZombieTreat extends EntitySkillLiving {

	public int remianTick = 0;

	public EntitySkilZombieTreat(EntityLivingBase entity) {
		super(entity);
		this.setCD(10 * 30);
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.3);
	}

	@Override
	public int doSkill() {
		super.doSkill();

		ItemStack item = new ItemStack(Items.SPLASH_POTION);
		List<PotionEffect> effects = new ArrayList<>(1);
		effects.add(new PotionEffect(MobEffects.INSTANT_DAMAGE, 20, 2));
		PotionUtils.appendEffects(item, effects);

		this.living.setHeldItem(EnumHand.MAIN_HAND, item);

		this.remianTick = 30;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

	@Override
	public void doSkillFin() {
		List<PotionEffect> effects = new ArrayList<>(1);
		effects.add(new PotionEffect(MobEffects.INSTANT_DAMAGE, 20, 1));
		WorldHelper.throwPotion(world, living, effects, false, 0.2f, 0);
		this.living.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
	}

	@Override
	public int doContinueSkill() {
		if (remianTick-- < 0) return EntitySkill.SKILL_RESULT_FIN;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

}
