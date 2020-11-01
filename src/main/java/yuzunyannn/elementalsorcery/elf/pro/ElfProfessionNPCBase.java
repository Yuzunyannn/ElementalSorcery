package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.entity.elf.EntityAIMoveToEntityItem;
import yuzunyannn.elementalsorcery.entity.elf.EntityAIMoveToLookBlock;
import yuzunyannn.elementalsorcery.entity.elf.EntityAIStrollAroundElfTree;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

/** 专指站着某个地点的npc */
public class ElfProfessionNPCBase extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.removeTask(EntityAIStrollAroundElfTree.class);
		elf.removeTask(EntityAIMoveToLookBlock.class);
		elf.removeTask(EntityAIMoveToEntityItem.class);
	}

	@Override
	public void tick(EntityElfBase elf) {
		if (elf.world.isRemote) return;
		if (elf.tick % 100 == 0) {
			BlockPos pos = elf.getHomePosition();
			if (!pos.equals(elf.getPosition())) {
				float x = pos.getX() + 0.5f;
				float y = pos.getY() + 0.1f;
				float z = pos.getZ() + 0.5f;
				elf.getNavigator().tryMoveToXYZ(x, y, z, 1);
			}
		}
	}

}
