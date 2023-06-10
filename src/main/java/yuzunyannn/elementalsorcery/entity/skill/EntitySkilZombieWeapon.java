package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import yuzunyannn.elementalsorcery.api.ESObjects;

/** 换武器！ */
public class EntitySkilZombieWeapon extends EntitySkillLiving {

	public EntitySkilZombieWeapon(EntityLivingBase entity) {
		super(entity);
		this.setCD(12 * 30);
	}

	@Override
	public boolean checkCanUse() {
		if (!super.checkCanUse()) return false;
		return this.living.getHeldItem(EnumHand.MAIN_HAND).isEmpty();
	}

	@Override
	public int doSkill() {
		super.doSkill();
		Random rand = getRandom();
		if (rand.nextFloat() > 0.5) {
			this.living.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));
		} else if (rand.nextFloat() > 0.5) {
			this.living.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
		} else {
			this.living.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ESObjects.ITEMS.MAGIC_GOLD_SWORD));
		}
		return EntitySkill.SKILL_RESULT_FIN;
	}

}
