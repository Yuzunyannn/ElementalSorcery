package yuzunyannn.elementalsorcery.summon;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonCategory;
import yuzunyannn.elementalsorcery.dungeon.DungeonRoomType;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.logics.EventServer;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class SummonDungeonRoom extends SummonCommon {

	protected int areaId;
	protected int roomId;
	protected byte step = 1;
	protected boolean isCreativeBuild;

	protected int iIter = 0;
	protected BuildingBlocks iter;
	protected DungeonAreaRoom room;
	protected DungeonCategory category = new DungeonCategory(new VariableSet());

	public SummonDungeonRoom(World world) {
		super(world, BlockPos.ORIGIN, 0xcccccc);
		category.setWorld(world);
	}

	public boolean isDevTest() {
		return areaId == -1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initRender() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doRender(Minecraft mc, float partialTicks) {
	}

	public void setCreativeBuild(boolean isCreativeBuild) {
		this.isCreativeBuild = isCreativeBuild;
	}

	public DungeonArea getArea() {
		return DungeonWorld.getDungeonWorld(world).getDungeon(areaId);
	}

	public DungeonAreaRoom getRoom() {
		if (room == null) {
			DungeonArea area = getArea();
			if (area == null) return null;
			room = area.getRoomById(roomId);
		}
		return room;
	}

	public void nextStep() {
		this.step++;
		DungeonAreaRoom room = getRoom();
		DungeonRoomType type = room.getType();
		this.iter = type.getStructure().getBuildingIterator();
		this.iter.setFace(room.getFacing());
		iIter = 0;
	}

	@Override
	public boolean update() {
		if (world.isRemote) return true;

		DungeonAreaRoom room = getRoom();
		if (room == null) return false;

		EventServer.bigComputeWatch.start();
		boolean ret = doUpdate();
		EventServer.bigComputeWatch.stop();

		return ret;
	}

	protected boolean doUpdate() {
		if (this.iter == null) {
			DungeonRoomType type = room.getType();
			this.iter = type.getStructure().getBuildingIterator();
			this.iter.setFace(room.getFacing());
		}

		switch (step) {
		case 0:
			nextStep();
			break;
		case 1:// 第一步，清理垃圾
			updateClear();
			break;
		case 2:// 第二步，构建基建
			updateBuildBase();
			break;
		case 3:// 第三步，构建核心陷阱等
			updateBuildCore();
			break;
		case 4:// 构建完成回调
			updateTasks();
			break;
		case 5:// 第四步，开门
			updateDoorOpen();
			break;
		default:
			return false;
		}

		return true;
	}

	public void updateClear() {
		// 开始用的是递归，dev模式下想瞬间完成，但是大建筑栈溢出了wwww，
		while (_updateClear());
	}

	public void updateBuildBase() {
		while (_updateBuildBase());
	}

	public void updateTasks() {
		while (true) {
			Runnable runable = category.popCompleteTask();
			if (runable == null) break;
			runable.run();
			if (EventServer.bigComputeWatch.msBiggerThan(DungeonWorld.DUNGEON_BUILDING_BLOCK_SPEED_LIMIT)) return;
		}
		nextStep();
	}

	protected boolean fin(double ms) {
		if (ms < 0) return true;
		if (EventServer.bigComputeWatch.msBiggerThan(ms)) return false;
		return true;
	}

	public void updateBuildCore() {

		DungeonAreaRoom room = getRoom();
		DungeonRoomType type = room.getType();

		IBlockState state;
		while (iter.next()) {
			state = iter.getState();
			BlockPos pos = iter.getPos();
			BlockPos at = pos.add(room.getCenterPos());
			type.buildCoreBlock(world, room, at, state, iter.getTileNBTSave(), category); // 一类方块也可能被赋予数据
		}

		nextStep();
	}

	public void updateDoorOpen() {
		DungeonArea area = getArea();
		DungeonAreaRoom room = getRoom();
		area.onRoomBuildFinish(world, room);
		nextStep();
	}

	protected boolean _updateClear() {
		DungeonAreaRoom room = getRoom();
		AxisAlignedBB box = room.getBox();

		int minX = MathHelper.ceil(box.minX);
		int maxX = MathHelper.ceil(box.maxX);
		int minY = MathHelper.ceil(box.minY);
		int maxY = MathHelper.ceil(box.maxY);
		int minZ = MathHelper.ceil(box.minZ);
		int maxZ = MathHelper.ceil(box.maxZ);

		int lenX = maxX - minX;
		int lenY = maxY - minY;
		int lenZ = maxZ - minZ;

		if (iIter >= lenX * lenZ * lenY) {
			nextStep();
			return false;
		}

		int xxz = (lenX * lenZ);
		int y = iIter / xxz;
		int x = (iIter % xxz) % lenX;
		int z = (iIter % xxz) / lenX;

		iIter++;

		BlockPos pos = new BlockPos(minX + x, minY + y, minZ + z);

//		if (world.isAirBlock(pos)) return fin(DungeonWorld.DUNGEON_BUILDING_CLEAR_SPEED_LIMIT);
		if (world.isAirBlock(pos)) return true;

		if (!BlockHelper.isBedrock(world, pos)) {
			if (isCreativeBuild) world.setBlockToAir(pos);
			else {
				if (BlockHelper.isOrdinaryBlock(world, pos)) world.setBlockToAir(pos);
				else world.destroyBlock(pos, true);
			}
		}

		return fin(DungeonWorld.DUNGEON_BUILDING_CLEAR_SPEED_LIMIT);
	}

	public boolean _updateBuildBase() {
		DungeonAreaRoom room = getRoom();
		DungeonRoomType type = room.getType();

		IBlockState state;
		while (true) {
			if (!iter.next()) {
				nextStep();
				return false;
			}
			state = iter.getState();
			if (type.isBaseBlockType(world, state)) break;
		}

		BlockPos pos = iter.getPos();
		BlockPos at = pos.add(room.getCenterPos());

		if (!type.buildBaseBlock(world, room, at, state, iter.getTileNBTSave())) iter.buildState(world, at);

		return fin(DungeonWorld.DUNGEON_BUILDING_BLOCK_SPEED_LIMIT);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("areaId", this.areaId);
		nbt.setInteger("roomId", this.roomId);
		nbt.setByte("step", step);
		nbt.setTag("category", category.serializeNBT());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.areaId = nbt.getInteger("areaId");
		this.roomId = nbt.getInteger("roomId");
		this.step = nbt.getByte("step");
		this.category.deserializeNBT(nbt.getCompoundTag("category"));
	}

}
