package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ElfProfessionNone extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
	}

	@Override
	public int attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		if (elf.world.isRemote) return 0;
		if (elf.getRNG().nextInt(100) < 6) elf.setProfession(ElfProfession.MASTER);
		else if (elf.getRNG().nextInt(3) == 0) elf.setProfession(ElfProfession.BERSERKER);
		else elf.setProfession(ElfProfession.WARRIOR);
		return 0;
	}

}
