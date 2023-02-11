package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class DungeonRoomDoor {

	protected BlockPos corePos;
	protected EnumFacing orient;
	protected int expandUp = 0;
	protected int expandDown = 0;
	protected int expandLeft = 0;
	protected int expandRight = 0;

	public DungeonRoomDoor(BlockPos corePos, EnumFacing facing) {
		this.corePos = corePos;
		this.orient = facing;
	}

	public BlockPos getCorePos() {
		return corePos;
	}

	public EnumFacing getOrient() {
		return orient;
	}

	public AxisAlignedBB getDoorBox(int length) {
		int x = corePos.getX();
		int y = corePos.getY();
		int z = corePos.getZ();
		int minX = x;
		int minY = y - expandDown;
		int minZ = z;
		int maxX = x;
		int maxY = y + expandUp;
		int maxZ = z;
		Vec3i vec = orient.getDirectionVec();
		int dx = vec.getX();
		int dz = vec.getZ();
		if (dx != 0) {
			if (dx > 0) {
				maxX += length;
				minZ -= expandLeft;
				maxZ += expandRight;
			} else {
				minX -= length;
				minZ -= expandRight;
				maxZ += expandLeft;
			}
		} else if (dz != 0) {
			if (dz > 0) {
				maxZ += length;
				minX -= expandRight;
				maxX += expandLeft;
			} else {
				minZ -= length;
				minX -= expandLeft;
				maxX += expandRight;
			}
		} else throw new IllegalArgumentException("orient msg be horizontal!");

		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public int getDoorLenghToBorder(AxisAlignedBB roomBox) {
		int length = 0;
		switch (orient) {
		case NORTH:
			length = corePos.getZ() - (int) roomBox.minZ;
			break;
		case SOUTH:
			length = (int) roomBox.maxZ - corePos.getZ();
			break;
		case EAST:
			length = (int) roomBox.maxX - corePos.getX();
			break;
		case WEST:
			length = corePos.getX() - (int) roomBox.minX;
			break;
		default:
			throw new IllegalArgumentException("orient msg be horizontal!");
		}
		return length;
	}

	public int getDoorWidth() {
		return 1 + expandLeft + expandRight;
	}

	public int getDoorHeight() {
		return 1 + expandUp + expandDown;
	}

}
