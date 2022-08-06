package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class EntityAIHarvestBlock extends EntityAIBase {

	final EntityElfBase elf;
	private BlockPos harvestPos;
	private int harvestTime;

	public EntityAIHarvestBlock(EntityElfBase elf) {
		this.elf = elf;
		this.setMutexBits(4);
	}

	@Override
	public boolean shouldExecute() {
		harvestPos = EntityAILookBlock.getLookPos(elf);
		return this.canHarvest(harvestPos);
	}

	@Override
	public void startExecuting() {
		harvestTime = 20 + elf.getRNG().nextInt(20);
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.canHarvest(harvestPos) && harvestTime > 0;
	}

	@Override
	public void updateTask() {
		harvestTime--;
		if (harvestTime <= 0) elf.tryHarvestBlock(harvestPos);
	}

	protected boolean canHarvest(BlockPos pos) {
		if (pos == null) return false;
		if (elf.world.getBlockState(pos).getBlock() == ESObjects.BLOCKS.ELF_FRUIT) return true;
		return false;
	}
}
