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
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonRoomType;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class SummonDungeonRoom extends SummonCommon {

	protected int areaId;
	protected int roomId;
	protected byte step = 1;

	protected int iIter = 0;
	protected BuildingBlocks iter;
	protected DungeonAreaRoom room;

	public SummonDungeonRoom(World world) {
		super(world, BlockPos.ORIGIN, 0xcccccc);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initRender() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doRender(Minecraft mc, float partialTicks) {
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
		DungeonAreaRoom room = getRoom();
		if (room == null) return false;

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
		case 4:// 第四步，开门
			updateDoorOpen();
			break;
		default:
			return false;
		}

		return true;
	}

	public void updateClear() {
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
			return;
		}

		int xxz = (lenX * lenZ);
		int y = iIter / xxz;
		int x = (iIter % xxz) % lenX;
		int z = (iIter % xxz) / lenX;

		iIter++;

		BlockPos pos = new BlockPos(minX + x, minY + y, minZ + z);

		if (world.isAirBlock(pos)) {
			if (Math.random() > 0.02) updateClear();
			return;
		}

		if (!BlockHelper.isBedrock(world, pos)) world.destroyBlock(pos, true);
		if (Math.random() > 0.2) updateClear();
	}

	public void updateBuildBase() {
		DungeonAreaRoom room = getRoom();
		DungeonRoomType type = room.getType();

		IBlockState state;
		while (true) {
			if (!iter.next()) {
				nextStep();
				return;
			}
			state = iter.getState();
			if (type.isBaseBlockType(world, state)) break;
		}

		BlockPos pos = iter.getPos();
		BlockPos at = pos.add(room.getCenterPos());

		if (!type.buildBaseBlock(world, room, at, state, iter.getTileNBTSave())) iter.buildState(world, at);

		if (Math.random() > 0.2) updateBuildBase();
	}

	public void updateBuildCore() {

		DungeonAreaRoom room = getRoom();
		DungeonRoomType type = room.getType();

		IBlockState state;
		while (iter.next()) {
			state = iter.getState();
			if (type.isBaseBlockType(world, state)) continue;
			BlockPos pos = iter.getPos();
			BlockPos at = pos.add(room.getCenterPos());
			type.buildCoreBlock(world, room, at, state, iter.getTileNBTSave());
		}

		nextStep();
	}

	public void updateDoorOpen() {
		DungeonArea area = getArea();
		DungeonAreaRoom room = getRoom();
		area.onRoomBuildFinish(world, room);
		nextStep();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("areaId", this.areaId);
		nbt.setInteger("roomId", this.roomId);
		nbt.setByte("step", step);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.areaId = nbt.getInteger("areaId");
		this.roomId = nbt.getInteger("roomId");
		this.step = nbt.getByte("step");
	}

}
