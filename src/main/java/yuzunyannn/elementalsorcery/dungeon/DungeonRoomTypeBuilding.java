package yuzunyannn.elementalsorcery.dungeon;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonDoor;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.tile.TileDungeonDoor;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class DungeonRoomTypeBuilding extends DungeonRoomType {

	protected final Building structure;

	public DungeonRoomTypeBuilding(Building structure) {
		this.structure = structure;
		this.init();
	}

	public Building getStructure() {
		return structure;
	}

	protected void init() {
		this.buildingBox = structure.getBox().expand(0.5, 0.5, 0.5);
		BuildingBlocks iter = structure.getBuildingIterator();
		while (iter.next()) {
			IBlockState state = iter.getState();
			Block block = state.getBlock();
			if (block == ESObjects.BLOCKS.DUNGEON_DOOR) initDoor(iter.getPos());

		}
	}
	
	@Override
	public AxisAlignedBB getBuildingBox() {
		return super.getBuildingBox();
	}

	/*----------------------
	 * 		初始化统计部分
	 * ------------------------*/

	protected boolean isDoorExpand(BlockPos pos) {
		Building.BlockInfo info = structure.getBlockInfo(pos);
		if (info == null) return false;
		return info.getState().getBlock() == ESObjects.BLOCKS.DUNGEON_DOOR_EXPAND;
	}

	protected int checkDoorExpand(DungeonRoomDoor door, EnumFacing facing, BlockPos pos) {
		int length = 0;
		pos = pos.offset(facing);
		while (isDoorExpand(pos)) {
			length++;
			if (facing.getHorizontalIndex() >= 0) {
				door.expandUp = Math.max(door.expandUp, checkDoorExpand(door, EnumFacing.UP, pos));
				door.expandDown = Math.max(door.expandDown, checkDoorExpand(door, EnumFacing.DOWN, pos));
			}
			pos = pos.offset(facing);
		}
		return length;
	}

	protected void initDoor(BlockPos pos) {
		Building.BlockInfo info = structure.getBlockInfo(pos);
		EnumFacing facing = info.getState().getValue(BlockDungeonDoor.FACING).getOpposite();
		DungeonRoomDoor door = new DungeonRoomDoor(pos, facing);
		EnumFacing fFacing = facing.rotateY();
		EnumFacing sFacing = fFacing.getOpposite();
		door.expandUp = Math.max(door.expandUp, checkDoorExpand(door, EnumFacing.UP, pos));
		door.expandDown = Math.max(door.expandDown, checkDoorExpand(door, EnumFacing.DOWN, pos));
		door.expandRight = checkDoorExpand(door, sFacing, pos);
		door.expandLeft = checkDoorExpand(door, sFacing, pos);
		this.doors.add(door);
	}

	@Override
	public void build(World world, DungeonArea area, DungeonAreaRoom room) {
		BuildingBlocks iter = structure.getBuildingIterator();
		iter.setFace(room.facing);
		while (iter.next()) {
			BlockPos pos = iter.getPos();
			BlockPos at = pos.add(room.at);
			IBlockState state = iter.getState();
			Block block = state.getBlock();

			if (block == ESObjects.BLOCKS.DUNGEON_DOOR || block == ESObjects.BLOCKS.DUNGEON_DOOR_EXPAND) {
				NBTTagCompound nbt = null;
				if (block == ESObjects.BLOCKS.DUNGEON_DOOR) {
					nbt = iter.getTileNBTSave();
					if (nbt == null) nbt = new NBTTagCompound();
				}
				buildDungeonDoor(world, room, at, nbt);
				continue;
			}

			iter.buildState(world, at);
		}

	}

	protected void buildDungeonDoor(World world, DungeonAreaRoom room, BlockPos at, @Nullable NBTTagCompound coreNBT) {
		boolean isBlock = false;
		int doorIndex = findDoorIndex(room, at);
		if (doorIndex != -1) {
			DungeonAreaDoor aDoor = room.doorLinks.get(doorIndex);
			isBlock = !aDoor.isLink();
		} else {
			ESAPI.logger.warn("查询门算法异常");
			isBlock = true;
		}
		if (isBlock) {
			world.setBlockState(at, Blocks.STONE.getDefaultState());
			return;
		}

		if (coreNBT == null) world.setBlockState(at, ESObjects.BLOCKS.DUNGEON_DOOR_EXPAND.getDefaultState());
		else {
			EnumFacing facing = BuildingFace.face(doors.get(doorIndex).orient, room.facing);
			IBlockState doorState = ESObjects.BLOCKS.DUNGEON_DOOR.getDefaultState();
			doorState = doorState.withRotation(BuildingFace.fromFacing(facing.getOpposite()));
			world.setBlockState(at, doorState);
			TileDungeonDoor tileDoor = BlockHelper.getTileEntity(world, at, TileDungeonDoor.class);
			tileDoor.initByDungeon(room, doorIndex, coreNBT);
		}
	}

	protected int findDoorIndex(DungeonAreaRoom room, BlockPos at) {
		EnumFacing facing = room.facing;
		for (int doorIndex = 0; doorIndex < doors.size(); doorIndex++) {
			DungeonRoomDoor door = doors.get(doorIndex);
			AxisAlignedBB doorBox = door.getDoorBox(door.getDoorLenghToBorder(buildingBox));
			doorBox = doorBox.expand(0.1, 0.1, 0.1).expand(-0.1, -0.1, -0.1);
			doorBox = BuildingFace.face(doorBox, facing);
			doorBox = doorBox.offset(room.at);
			if (doorBox.contains(new Vec3d(at))) return doorIndex;
		}
		return -1;
	}

}
