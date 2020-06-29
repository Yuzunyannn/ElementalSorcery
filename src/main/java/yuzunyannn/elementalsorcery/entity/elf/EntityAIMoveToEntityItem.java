package yuzunyannn.elementalsorcery.entity.elf;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EntityAIMoveToEntityItem extends EntityAIBase {

	private final EntityElfBase elf;
	private float chance = 0.025f;
	private int width = 6;
	private int height = 2;
	private BlockPos toPos;
	private double movementSpeed = 1.0f;

	public EntityAIMoveToEntityItem(EntityElfBase elf) {
		this.elf = elf;
		this.setMutexBits(1);
	}

	public EntityAIMoveToEntityItem setMovementSpeed(double movementSpeed) {
		this.movementSpeed = movementSpeed;
		return this;
	}

	public EntityAIMoveToEntityItem setChance(float chance) {
		this.chance = chance;
		return this;
	}

	public EntityAIMoveToEntityItem setMaxDistance(int width) {
		this.width = width;
		return this;
	}

	@Override
	public boolean shouldExecute() {
		if (this.elf.getRNG().nextFloat() >= this.chance) return false;
		AxisAlignedBB aabb = new AxisAlignedBB(elf.posX - width, elf.posY - height, elf.posZ - width, elf.posX + width,
				elf.posY + height, elf.posZ + width);
		List<EntityItem> list = elf.world.getEntitiesWithinAABB(EntityItem.class, aabb);
		for (EntityItem item : list) {
			if (elf.getProfession().needPickup(elf, item.getItem())) {
				toPos = item.getPosition();
				break;
			}
		}
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
