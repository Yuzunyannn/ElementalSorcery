package yuzunyannn.elementalsorcery.building;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.block.env.BlockStoneDecoration;

public class BuildingFace {

	static public Rotation fromFacing(EnumFacing facing) {
		switch (facing) {
		case SOUTH:
			return Rotation.CLOCKWISE_180;
		case EAST:
			return Rotation.CLOCKWISE_90;
		case WEST:
			return Rotation.COUNTERCLOCKWISE_90;
		default:
			return Rotation.NONE;
		}
	}

	static public EnumFacing fromRotation(Rotation rotation) {
		return rotation.rotate(EnumFacing.NORTH);
	}

	@SuppressWarnings("incomplete-switch")
	static public Rotation getRoation(EnumFacing currFacing, EnumFacing targetFacing) {

		if (targetFacing == EnumFacing.NORTH) {
			switch (currFacing) {
			case NORTH:
				return Rotation.NONE;
			case SOUTH:
				return Rotation.CLOCKWISE_180;
			case WEST:
				return Rotation.CLOCKWISE_90;
			case EAST:
				return Rotation.COUNTERCLOCKWISE_90;
			}
		} else if (targetFacing == EnumFacing.SOUTH) {
			switch (currFacing) {
			case NORTH:
				return Rotation.CLOCKWISE_180;
			case SOUTH:
				return Rotation.NONE;
			case WEST:
				return Rotation.COUNTERCLOCKWISE_90;
			case EAST:
				return Rotation.CLOCKWISE_90;
			}
		} else if (targetFacing == EnumFacing.EAST) {
			switch (currFacing) {
			case NORTH:
				return Rotation.CLOCKWISE_90;
			case SOUTH:
				return Rotation.COUNTERCLOCKWISE_90;
			case WEST:
				return Rotation.CLOCKWISE_180;
			case EAST:
				return Rotation.NONE;
			}
		} else if (targetFacing == EnumFacing.WEST) {
			switch (currFacing) {
			case NORTH:
				return Rotation.COUNTERCLOCKWISE_90;
			case SOUTH:
				return Rotation.CLOCKWISE_90;
			case WEST:
				return Rotation.NONE;
			case EAST:
				return Rotation.CLOCKWISE_180;
			}
		}

		throw new IllegalArgumentException("facing not horizontal");
	}

	/** 根据方向修正pos */
	static public BlockPos face(BlockPos pos, EnumFacing rotation) {
		return face(pos, fromFacing(rotation));
	}

	static public BlockPos face(BlockPos pos, Rotation facing) {
		switch (facing) {
		case CLOCKWISE_180:
			return new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
		case CLOCKWISE_90:
			return new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
		case COUNTERCLOCKWISE_90:
			return new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
		default:
			return pos;
		}
	}

	static public Vec3d face(Vec3d pos, EnumFacing rotation) {
		return face(pos, fromFacing(rotation));
	}

	static public Vec3d face(Vec3d pos, Rotation facing) {
		switch (facing) {
		case CLOCKWISE_180:
			return new Vec3d(-pos.x, pos.y, -pos.z);
		case CLOCKWISE_90:
			return new Vec3d(-pos.z, pos.y, pos.x);
		case COUNTERCLOCKWISE_90:
			return new Vec3d(pos.z, pos.y, -pos.x);
		default:
			return pos;
		}
	}

	static public EnumFacing face(EnumFacing facing, EnumFacing rotation) {
		return fromFacing(rotation).rotate(facing);
	}

	static public EnumFacing face(EnumFacing facing, Rotation rotation) {
		return rotation.rotate(facing);
	}

	/** 根据方向修正state */
	static public IBlockState face(IBlockState state, EnumFacing rotation) {
		return state.withRotation(fromFacing(rotation));
	}

	static public IBlockState face(IBlockState state, Rotation rotation) {
		return state.withRotation(rotation);
	}

	public static AxisAlignedBB face(AxisAlignedBB box, EnumFacing facing) {
		return face(box, fromFacing(facing));
	}

	public static AxisAlignedBB face(AxisAlignedBB box, Rotation facing) {
		Vec3d pos1 = face(new Vec3d(box.minX, box.minY, box.minZ), facing);
		Vec3d pos2 = face(new Vec3d(box.maxX, box.maxY, box.maxZ), facing);
		double minX = Math.min(pos1.x, pos2.x);
		double minY = Math.min(pos1.y, pos2.y);
		double minZ = Math.min(pos1.z, pos2.z);
		double maxX = Math.max(pos1.x, pos2.x);
		double maxY = Math.max(pos1.y, pos2.y);
		double maxZ = Math.max(pos1.z, pos2.z);
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/** 根据方向修正skull数值 */
	static public int faceRot(int rot, EnumFacing facing) {
		switch (facing) {
		case SOUTH:
			return (rot + 8) % 16;
		case EAST:
			return (rot + 4) % 16;
		case WEST:
			return (rot - 4 + 16) % 16;
		default:
			return rot;
		}
	}

	static public NBTTagCompound tryFaceTile(IBlockState state, NBTTagCompound save, EnumFacing facing,
			boolean needCopy) {
		Block block = state.getBlock();
		if (block instanceof BlockSkull) {
			if (needCopy) save = save.copy();
			save.setByte("Rot", (byte) BuildingFace.faceRot(save.getByte("Rot"), facing));
		} else if (block instanceof BlockStoneDecoration) {
			if (needCopy) save = save.copy();
			EnumFacing myFacing = EnumFacing.byHorizontalIndex(save.getInteger("facing"));
			save.setByte("facing", (byte) face(myFacing, facing).getHorizontalIndex());
		}
		return save;
	}

}
