package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class ElfProfessionWarrior extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInitInstance.ITEMS.KYANITE_SWORD));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
	}

	@Override
	public int attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		if (!source.isUnblockable()) {
			if (elf.getRNG().nextInt(5) == 0) {
				elf.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + elf.world.rand.nextFloat() * 0.4F);
				return -1;
			}
		}
		return 0;
	}
}
