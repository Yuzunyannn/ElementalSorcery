package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

/** 换武器！ */
public class EntitySkilZombieEquip extends EntitySkillLiving {

	public EntitySkilZombieEquip(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 30);
	}

	@Override
	public boolean checkCanUse() {
		if (!super.checkCanUse()) return false;
		if (!checkHPLowerThan(0.75)) return false;
		return this.living.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty();
	}

	@Override
	public int doSkill() {
		super.doSkill();

		Random rand = this.getRandom();

		if (rand.nextFloat() > 0.5) {
			this.living.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
			this.living.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
			this.living.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
			this.living.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
			world.playSound(null, living.posX, living.posY, living.posZ, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
					SoundCategory.AMBIENT, 5, 1);
		} else if (rand.nextFloat() > 0.5) {
			this.living.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
			this.living.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
			this.living.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
			this.living.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
			world.playSound(null, living.posX, living.posY, living.posZ, SoundEvents.ITEM_ARMOR_EQUIP_IRON,
					SoundCategory.AMBIENT, 5, 1);
		} else {
			this.living.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
			this.living.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
			this.living.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
			this.living.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
			world.playSound(null, living.posX, living.posY, living.posZ, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
					SoundCategory.AMBIENT, 5, 1);
		}

		return EntitySkill.SKILL_RESULT_FIN;
	}

}
