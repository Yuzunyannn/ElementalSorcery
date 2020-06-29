package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIMoveToLookBlock extends EntityAIBase {

	private final EntityCreature creature;
	private BlockPos watchPos;
	private double movementSpeed = 1.0f;

	public EntityAIMoveToLookBlock(EntityCreature creatureIn) {
		this.creature = creatureIn;
		this.setMutexBits(1);
	}

	public EntityAIMoveToLookBlock setMovementSpeed(double movementSpeed) {
		this.movementSpeed = movementSpeed;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		watchPos = EntityAILookBlock.getLookPos(creature);
		return watchPos != null && !creature.world.isAirBlock(watchPos);
	}

	@Override
	public boolean shouldContinueExecuting() {
		return watchPos != null && !creature.world.isAirBlock(watchPos) && !this.creature.getNavigator().noPath();
	}

	public void startExecuting() {
		this.creature.getNavigator().tryMoveToXYZ(watchPos.getX(), watchPos.getY(), watchPos.getZ(),
				this.movementSpeed);
	}

}
