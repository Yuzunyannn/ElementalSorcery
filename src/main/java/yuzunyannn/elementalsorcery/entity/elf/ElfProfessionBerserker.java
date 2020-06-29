package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class ElfProfessionBerserker extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInitInstance.ITEMS.KYANITE_SWORD));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ESInitInstance.ITEMS.KYANITE_SWORD));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
	}

	@Override
	public int attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		elf.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 20 * 10, 2));
		elf.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 10, 3));
		elf.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 10, 4));
		elf.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 10));
		return 0;
	}
}
