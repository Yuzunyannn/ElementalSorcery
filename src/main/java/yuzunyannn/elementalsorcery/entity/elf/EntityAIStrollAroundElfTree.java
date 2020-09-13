package yuzunyannn.elementalsorcery.entity.elf;

import java.util.Random;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;

public class EntityAIStrollAroundElfTree extends EntityAIBase {

	private final EntityElfBase elf;
	private float chance = 0.025f;
	private BlockPos toPos;
	private double movementSpeed = 1.0f;

	public EntityAIStrollAroundElfTree(EntityElfBase elf) {
		this.elf = elf;
		this.setMutexBits(1);
	}

	public EntityAIStrollAroundElfTree setMovementSpeed(double movementSpeed) {
		this.movementSpeed = movementSpeed;
		return this;
	}

	public EntityAIStrollAroundElfTree setChance(float chance) {
		this.chance = chance;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		if (this.elf.getRNG().nextFloat() >= this.chance) return false;
		if (this.elf.getTalker() != null) return false;
		TileElfTreeCore core = elf.getEdificeCore();
		if (core == null) return false;
		int treeSize = core.getTreeSize();
		Random rand = this.elf.getRNG();
		BlockPos pos = core.getTreeBasicPos();
		int x = (int) this.elf.posX + rand.nextInt(10) - 5;
		int z = (int) this.elf.posZ + rand.nextInt(10) - 5;
		if (Math.abs(x - pos.getX()) >= treeSize * 3) return false;
		if (Math.abs(z - pos.getZ()) >= treeSize * 3) return false;
		pos = new BlockPos(x, this.elf.posY, z);
		if (this.elf.world.isAirBlock(pos)) while (this.elf.world.isAirBlock(pos) && pos.getY() > 0) pos = pos.down();
		else while (this.elf.world.isAirBlock(pos) && pos.getY() < 200) pos = pos.up();
		toPos = pos;
		return toPos != null;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.elf.getNavigator().noPath();
	}

	public void startExecuting() {
		this.elf.getNavigator().tryMoveToXYZ(toPos.getX(), toPos.getY(), toPos.getZ(), this.movementSpeed);
	}

}
