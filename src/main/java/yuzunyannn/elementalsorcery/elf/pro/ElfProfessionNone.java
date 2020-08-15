package yuzunyannn.elementalsorcery.elf.pro;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

public class ElfProfessionNone extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
	}

	@Override
	public int attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		if (elf.world.isRemote) return 0;
		if (source.getTrueSource() instanceof EntityLivingBase == false) return 0;
		if (elf.getRNG().nextInt(100) < 4) elf.setProfession(ElfProfession.MASTER);
		else if (elf.getRNG().nextInt(3) == 0) elf.setProfession(ElfProfession.BERSERKER);
		else elf.setProfession(ElfProfession.WARRIOR);
		// 吸怪
		final int size = 16;
		AxisAlignedBB aabb = new AxisAlignedBB(elf.posX - size, elf.posY - size, elf.posZ - size, elf.posX + size,
				elf.posY + size, elf.posZ + size);
		List<EntityElf> list = elf.world.getEntitiesWithinAABB(EntityElf.class, aabb);
		for (EntityElf e : list)
			if (e.getRevengeTarget() == null) e.setRevengeTarget((EntityLivingBase) source.getTrueSource());
		return 0;
	}

}
