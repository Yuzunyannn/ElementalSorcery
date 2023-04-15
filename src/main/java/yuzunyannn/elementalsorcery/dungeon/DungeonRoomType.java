package yuzunyannn.elementalsorcery.dungeon;

import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.util.ESImplRegister;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonBrick;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonDoor;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.dungeon.DungeonFuncGlobal.GroupInfo;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonDoor;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper.WeightRandom;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonRoomType extends IForgeRegistryEntry.Impl<DungeonRoomType> {

	public static final ESImplRegister<DungeonRoomType> REGISTRY = new ESImplRegister(ElfProfession.class);

	static public final AxisAlignedBB ZERO_BOX = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	protected final Building structure;
	protected final AxisAlignedBB buildingBox;
	protected List<DungeonRoomDoor> doors = new ArrayList<>();
	protected List<Entry<BlockPos, String>> funcs = new ArrayList<>();
	protected DungeonFuncGlobal funcGlobal = null;

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
			else if (block == ESObjects.BLOCKS.DUNGEON_FUNCTION) initFunc(iter.getPos());
		}
	}

	@Nullable
	public DungeonFuncGlobal getFuncGlobal() {
		return funcGlobal;
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
		door.expandRight = checkDoorExpand(door, fFacing, pos);
		door.expandLeft = checkDoorExpand(door, sFacing, pos);
		this.doors.add(door);
	}

	protected void initFunc(BlockPos pos) {
		Building.BlockInfo info = structure.getBlockInfo(pos);
		NBTTagCompound tileSave = info.getTileEntityNBTData();
		if (tileSave == null) return;

		String dungeonConfig = tileSave.getString("dungeon_config");
		if (dungeonConfig.isEmpty()) return;

		funcs.add(new AbstractMap.SimpleEntry(pos, dungeonConfig));

		GameFunc func = GameFunc.create(new JsonObject(dungeonConfig));
		if (func instanceof DungeonFuncGlobal) {
			if (this.funcGlobal != null) throw new RuntimeException("mutil global func!");
			this.funcGlobal = (DungeonFuncGlobal) func;
		}
	}

	/*----------------------
	 * 		规划构建部分
	 * ------------------------*/

	public void onInitRoom(DungeonAreaRoom room, Random rand) {
		DungeonFuncGlobal global = room.getFuncGlobal();
		if (global == null) global = new DungeonFuncGlobal();
		List<GameFunc> funcs = room.getFuncs();
		Map<String, WeightRandom<Entry<Integer, GameFunc>>> groupRandomMap = new TreeMap<>();
		for (int i = 0; i < funcs.size(); i++) {
			GameFunc func = funcs.get(i);
			if (func == GameFunc.NOTHING) continue;
			// 有概率，优先处理概率排除
			if (func.hasConfig(GameFunc.PROBABILITY)) {
				float probability = func.getConfig(GameFunc.PROBABILITY);
				if (probability < rand.nextFloat()) {
					funcs.set(i, GameFunc.NOTHING);
					continue;
				}
			}
			// 记录组，等待后面处理
			if (func.hasConfig(GameFunc.GROUP_NAME)) {
				String name = func.getConfig(GameFunc.GROUP_NAME);
				WeightRandom<Entry<Integer, GameFunc>> wr = groupRandomMap.get(name);
				if (wr == null) groupRandomMap.put(name, wr = new WeightRandom());
				float weight = func.getConfig(GameFunc.GROUP_WEIGHT);
				if (weight <= 0) weight = 1;
				wr.add(new AbstractMap.SimpleEntry(i, func), weight);
				funcs.set(i, GameFunc.NOTHING);
				continue;
			}
		}
		// 处理组随机
		Map<String, List<Integer>> groupMap = new TreeMap<>();
		for (Entry<String, WeightRandom<Entry<Integer, GameFunc>>> entry : groupRandomMap.entrySet()) {
			String groupName = entry.getKey();
			WeightRandom<Entry<Integer, GameFunc>> wr = entry.getValue();
			int maxCount = wr.size();
			int minCount = 1;
			GroupInfo info = global.getGroupInfo(groupName);
			if (info != null) {
				maxCount = Math.min(maxCount, info.maxCount);
				minCount = Math.max(info.minCount, 0);
			}
			int count = MathHelper.getInt(rand, minCount, maxCount);
			if (count == 0) continue;

			for (int i = 0; i < count; i++) {
				Entry<Integer, GameFunc> ifunc = wr.get(rand, true);
				funcs.set(ifunc.getKey(), ifunc.getValue());
				List<Integer> list = groupMap.get(groupName);
				if (list == null) groupMap.put(groupName, list = new ArrayList<>());
				list.add(ifunc.getKey());
			}
		}
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
		if (block == ESObjects.BLOCKS.DUNGEON_FUNCTION) return false;
		return true;
	}

	public boolean buildBaseBlock(World world, DungeonAreaRoom room, BlockPos pos, IBlockState state,
			NBTTagCompound tileSave) {
		IBlockState newState = null;
		Block block = state.getBlock();

		// 特殊部分
		if (block == Blocks.FLOWER_POT) {
			world.setBlockState(pos, state);
			TileEntityFlowerPot flowerPot = BlockHelper.getTileEntity(world, pos, TileEntityFlowerPot.class);
			if (flowerPot != null) flowerPot.setItemStack(ItemHelper.randomFlower(world.rand));
			return true;
		}

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
		} else if (block == Blocks.STONE) {
			newState = ESObjects.BLOCKS.DUNGEON_BRICK.getDefaultState().withProperty(BlockDungeonBrick.VARIANT,
					BlockDungeonBrick.EnumType.STONE);
		}
		Random rand = world.rand;

		// 真是替换部分
		if (block == Blocks.BARRIER) {
			newState = ESObjects.BLOCKS.DUNGEON_BARRIER.getDefaultState();
		} else if (block == ESObjects.BLOCKS.DUNGEON_BRICK) {
			newState = state;
			BlockDungeonBrick.EnumType type = state.getValue(BlockDungeonBrick.VARIANT);
			if (type == BlockDungeonBrick.EnumType.DEFAULT) {
				if (rand.nextFloat() < 0.2)
					newState = newState.withProperty(BlockDungeonBrick.VARIANT, BlockDungeonBrick.EnumType.MOSSY);
				else if (rand.nextFloat() < 0.1)
					newState = newState.withProperty(BlockDungeonBrick.VARIANT, BlockDungeonBrick.EnumType.CRACKED);
			}
		} else if (block == Blocks.DIRT) {
			if (rand.nextFloat() < 0.4) {
				newState = Blocks.GRASS.getDefaultState();
			} else {
				if (rand.nextFloat() < 0.2)
					newState = state.withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
				else if (rand.nextFloat() < 0.2)
					newState = state.withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
			}
			if (rand.nextFloat() < 0.2 && world.isAirBlock(pos.up())) {
				IBlockState grass = Blocks.TALLGRASS.getDefaultState();
				BlockTallGrass.EnumType[] types = BlockTallGrass.EnumType.values();
				world.setBlockState(pos.up(),
						grass.withProperty(BlockTallGrass.TYPE, types[rand.nextInt(types.length)]));
			}
		} else if (block == Blocks.GLOWSTONE) {
			newState = ESObjects.BLOCKS.DUNGEON_LIGHT.getDefaultState();
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
		} else if (block == ESObjects.BLOCKS.DUNGEON_FUNCTION) {
			this.buildFunc(world, room, pos, tileSave);
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
			world.setBlockState(at, ESObjects.BLOCKS.DUNGEON_BRICK.getDefaultState()
					.withProperty(BlockDungeonBrick.VARIANT, BlockDungeonBrick.EnumType.STONE));
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

	protected WeakReference<Map<BlockPos, Integer>> tempPosMapRef = new WeakReference(null);

	protected Map<BlockPos, Integer> getTempPosFuncMap() {
		if (tempPosMapRef.get() == null) {
			Map<BlockPos, Integer> map = new HashMap<>();
			tempPosMapRef = new WeakReference(map);
			for (int i = 0; i < funcs.size(); i++) {
				Entry<BlockPos, String> entry = funcs.get(i);
				map.put(entry.getKey(), i);
			}
			return map;
		}
		return tempPosMapRef.get();
	}

	protected void buildFunc(World world, DungeonAreaRoom room, BlockPos at, NBTTagCompound coreNBT) {
		BlockPos pos = BuildingFace.face(at.subtract(room.at), BuildingFace.getRoation(room.facing, EnumFacing.NORTH));
		Map<BlockPos, Integer> map = getTempPosFuncMap();
		Integer index = map.get(pos);
		if (index == null) return;
		new DungeonFuncExecuteContextBuild(room, index, world, at).doExecute();
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
