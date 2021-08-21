package yuzunyannn.elementalsorcery.entity.elf;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIStrollAnyWhere extends EntityAIBase {

	private final EntityLiving entity;
	private float chance = 0.01f;
	private BlockPos toPos;
	private double movementSpeed = 1.0f;
	private float onceMoveRange = 5;

	public EntityAIStrollAnyWhere(EntityLiving elf) {
		this.entity = elf;
		this.setMutexBits(1);
	}

	public EntityAIStrollAnyWhere setMovementSpeed(double movementSpeed) {
		this.movementSpeed = movementSpeed;
		return this;
	}

	public EntityAIStrollAnyWhere setChance(float chance) {
		this.chance = chance;
		return this;
	}

	public EntityAIStrollAnyWhere setOnceMoveRange(float range) {
		this.onceMoveRange = range;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		if (this.entity.getRNG().nextFloat() >= this.chance) return false;
		Random rand = this.entity.getRNG();
		double x = this.entity.posX + rand.nextGaussian() * onceMoveRange;
		double z = this.entity.posZ + rand.nextGaussian() * onceMoveRange;
		BlockPos pos = new BlockPos(x, this.entity.posY, z);
		if (this.entity.world.isAirBlock(pos))
			while (this.entity.world.isAirBlock(pos) && pos.getY() > 0) pos = pos.down();
		else while (this.entity.world.isAirBlock(pos) && pos.getY() < 200) pos = pos.up();
		toPos = pos;
		return toPos != null;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.entity.getNavigator().noPath();
	}

	public void startExecuting() {
		this.entity.getNavigator().tryMoveToXYZ(toPos.getX(), toPos.getY(), toPos.getZ(), this.movementSpeed);
	}

}
