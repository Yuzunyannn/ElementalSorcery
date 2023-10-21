package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.entity.elf.EntityAIMoveToEntityItem;
import yuzunyannn.elementalsorcery.entity.elf.EntityAIMoveToLookBlock;
import yuzunyannn.elementalsorcery.entity.elf.EntityAIStrollAroundElfTree;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

/** 专指站着某个地点的npc */
public class ElfProfessionNPCBase extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		if (elf.world.isRemote) return;
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
			TileElfTreeCore core = elf.getEdificeCore();
			if (core == null) elf.setDead();
		}
		if (elf.tick % 20 == 0) {
			if (elf.getRevengeTarget() != null && ElfConfig.isPublicEnemy(elf.getRevengeTarget())) {
				BlockPos pos = WorldHelper.tryFindPlaceToSpawn(elf.world, elf.getRNG(),
						elf.getRevengeTarget().getPosition(), 2);
				if (pos != null) {
					EntityElfBase newElf = new EntityElf(elf.world);
					newElf.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
					newElf.setRevengeTarget(elf.getRevengeTarget());
					elf.onInitialSpawn(elf.world.getDifficultyForLocation(new BlockPos(elf)), null);
					elf.world.spawnEntity(newElf);
					elf.setRevengeTarget(null);
					elf.leave();
				}
			}
		}
	}

	public boolean canDespawn(EntityElfBase elf) {
		return false;
	}
}
