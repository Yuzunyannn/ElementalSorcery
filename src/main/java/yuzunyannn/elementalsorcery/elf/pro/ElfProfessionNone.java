package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

public class ElfProfessionNone extends ElfProfessionUndetermined {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
	}

	public boolean canDespawn(EntityElfBase elf) {
		return true;
	}

}
