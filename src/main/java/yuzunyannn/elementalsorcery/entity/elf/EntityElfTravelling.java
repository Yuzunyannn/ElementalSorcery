package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;

public class EntityElfTravelling extends EntityElf {

	public EntityElfTravelling(World worldIn) {
		super(worldIn, ElfProfession.MERCHANT);
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}

}
