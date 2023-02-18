package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.ESImplRegister;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonBrick;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonDoor;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.tile.TileDungeonDoor;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class DungeonRoomType extends IForgeRegistryEntry.Impl<DungeonRoomType> {

	public static final ESImplRegister<DungeonRoomType> REGISTRY = new ESImplRegister(ElfProfession.class);

	static public final AxisAlignedBB ZERO_BOX = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	protected final Building structure;
	protected final AxisAlignedBB buildingBox;
	protected List<DungeonRoomDoor> doors = new ArrayList<>();

	public DungeonRoomType(Building structure) {
		this.structure = structure;
		this.buildingBox = structure.getBox().expand(0.5, 0.5, 0.5).expand(-0.25, -0.25, -0.25);
		this.init();
	}

	protected void init() {
		BuildingBlocks iter = structure.getBuildingIterator();
		while (iter.next()) {
			IBlockState state = iter.getState();
			Block block = state.getBlock();
			if (block == ESObjects.BLOCKS.DUNGEON_DOOR) initDoor(iter.getPos());

		}
	}

	/** 获取建筑的静态大小 */
	public AxisAlignedBB getBuildingBox() {
		return buildingBox;
	}

	/** 获取建筑的房间可通行门，返回的数据应该是唯一的，下标作为id */
	public List<DungeonRoomDoor> getDoors() {
		return doors;
	}

	public Building getStructure() {
		return structure;
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

	/*----------------------
	 * 		build部分初始化统计部分
	 * ------------------------*/

	/**
	 * 是否为一类方块
	 */

	public boolean isBaseBlockType(World world, IBlockState state) {
		Block block = state.getBlock();
		if (block == ESObjects.BLOCKS.DUNGEON_DOOR || block == ESObjects.BLOCKS.DUNGEON_DOOR_EXPAND) return false;
		return true;
	}

	public boolean buildBaseBlock(World world, DungeonAreaRoom room, BlockPos pos, IBlockState state,
			NBTTagCompound tileSave) {
		IBlockState newState = null;
		Block block = state.getBlock();
		// 预替换部分
		if (block == Blocks.STONEBRICK) {
			BlockStoneBrick.EnumType type = state.getValue(BlockStoneBrick.VARIANT);
			state = ESObjects.BLOCKS.DUNGEON_BRICK.getDefaultState();
			if (type == EnumType.CHISELED)
				state = state.withProperty(BlockDungeonBrick.VARIANT, BlockDungeonBrick.EnumType.CHISELED);
			block = state.getBlock();
		} else if (block == Blocks.STONE_BRICK_STAIRS) {
			IBlockState nState = ESObjects.BLOCKS.DUNGEON_STAIRS.getDefaultState();
			nState = nState.withProperty(BlockStairs.FACING, state.getValue(BlockStairs.FACING));
			nState = nState.withProperty(BlockStairs.HALF, state.getValue(BlockStairs.HALF));
			nState = nState.withProperty(BlockStairs.SHAPE, state.getValue(BlockStairs.SHAPE));
			state = nState;
			block = state.getBlock();
			newState = state;
		}
		Random rand = world.rand;

		// 真是替换部分
		if (block == ESObjects.BLOCKS.DUNGEON_BRICK) {
			newState = state;
			BlockDungeonBrick.EnumType type = state.getValue(BlockDungeonBrick.VARIANT);
			if (type == BlockDungeonBrick.EnumType.DEFAULT) {
				if (rand.nextFloat() < 0.2)
					newState = newState.withProperty(BlockDungeonBrick.VARIANT, BlockDungeonBrick.EnumType.MOSSY);
				else if (rand.nextFloat() < 0.1)
					newState = newState.withProperty(BlockDungeonBrick.VARIANT, BlockDungeonBrick.EnumType.CRACKED);
			}
		}

		if (newState != null) {
			world.setBlockState(pos, newState);
			return true;
		}

		return false;
	}

	public void buildCoreBlock(World world, DungeonAreaRoom room, BlockPos pos, IBlockState state,
			NBTTagCompound tileSave) {
		Block block = state.getBlock();
		// 门
		if (block == ESObjects.BLOCKS.DUNGEON_DOOR || block == ESObjects.BLOCKS.DUNGEON_DOOR_EXPAND) {
			if (block == ESObjects.BLOCKS.DUNGEON_DOOR && tileSave == null) tileSave = new NBTTagCompound();
			this.buildDoor(world, room, pos, tileSave);
			return;
		}
	}

	protected void buildDoor(World world, DungeonAreaRoom room, BlockPos at, @Nullable NBTTagCompound coreNBT) {
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

	public void deubgBuild(World world, DungeonArea area, DungeonAreaRoom room) {
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
				buildDoor(world, room, at, nbt);
				continue;
			}

			iter.buildState(world, at);
		}

	}
}
