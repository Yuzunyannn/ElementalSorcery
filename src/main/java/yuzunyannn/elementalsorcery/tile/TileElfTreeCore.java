package yuzunyannn.elementalsorcery.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.edifice.BuildProgress;
import yuzunyannn.elementalsorcery.elf.edifice.BuilderWithInfo;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorHall;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.elf.edifice.FloorInfo;
import yuzunyannn.elementalsorcery.elf.edifice.FloorInfo.Status;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class TileElfTreeCore extends TileEntityNetwork implements ITickable {

	protected Random rand = new Random();

	/** 大厦高度 */
	int high;
	/** 大厦大小 */
	int size = 8;
	/** 大厦每层数据 */
	List<FloorInfo> floors = new ArrayList<>();

	/** 初始化数据 */
	public void initTreeData(int size, int high) {
		this.size = size;
		this.high = high;
	}

	// 虽然有数据同步，但客户端的数据仅作为显示使用！
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("high", high);
		nbt.setInteger("size", size);
		NBTTagList list = new NBTTagList();
		for (FloorInfo info : floors) list.appendTag(info.serializeNBT());
		nbt.setTag("floor", list);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		high = nbt.getInteger("high");
		size = nbt.getInteger("size");
		NBTTagList list = nbt.getTagList("floor", NBTTag.TAG_COMPOUND);
		floors.clear();
		for (NBTBase base : list) floors.add(new FloorInfo((NBTTagCompound) base));
		super.readFromNBT(nbt);
	}

	public int tick = 0;

	@Override
	public void update() {
		tick++;
		if (world.isRemote) return;
		try {
			tick();
		} catch (Exception e) {
			ElementalSorcery.logger.warn("精灵大厦核心出现异常！", e);
			EventServer.addTask(() -> {
				world.setBlockToAir(pos);
			});
		}
	}

	public void tick() {
		// 进行一次精灵生成（大厦脚下）
		// if (tick % 300 == 0) trySpawnElfAround();
		trySpawnElfAround();
		if (tick % 1200 == 0) checkBuildTaskState();
		if (tick % 600 == 0) tryBuildingSpawn();
	}

	public int getTreeSize() {
		return size;
	}

	public int getTreeHigh() {
		return high;
	}

	public BlockPos getTreeBasicPos() {
		return pos.down(high - 1);
	}

	public int getFloorHigh(int n) {
		int high = 0;
		for (int i = 0; i < floors.size() && i <= n; i++) {
			FloorInfo floor = floors.get(i);
			high += floor.getHigh() + 1;
		}
		return high;
	}

	public int getFloorCount() {
		return floors.size();
	}

	public FloorInfo getFloor(int index) {
		return floors.get(index);
	}

	// ----------------------------精灵建筑任务部分----------------------------

	public void tryBuildingSpawn() {
		int size = floors.size();
		int off = rand.nextInt(size);
		int n = rand.nextInt(size + 1);
		for (int i = 0; i < n; i++) {
			int index = (i + off) % size;
			FloorInfo floor = floors.get(index);
			if (floor.isEmpty()) continue;
			BuilderWithInfo builder = new BuilderWithInfo(world, floor, this.size, high, pos);
			floor.getType().spawn(builder);
		}
	}

	public void checkBuildTaskState() {
		for (int i = 0; i < floors.size(); i++) {
			FloorInfo floor = floors.get(i);
			if (floor.isEmpty()) continue;
			if (floor.getStatus() == Status.CONSTRUCTING) {
				BuildProgress progress = floor.getProgress();
				if (progress == null) continue;
				progress.subPeople();
			}
		}
	}

	/**
	 * 申请一个任务，一旦申请成功，请不要重复调用，申请的数据不必要进行持久化，重新加载后，重新申请就可以
	 * 
	 * @param justCheck 仅仅是检查，并不真实分配
	 * 
	 * @return 返回任务id及层数，-1表示申请失败
	 * 
	 */
	public int applyBuildTask(boolean justCheck) {
		if (floors.isEmpty()) this.scheduleFloor(EFloorHall.instance);
		int maybe = -1;
		for (int i = 0; i < floors.size(); i++) {
			FloorInfo floor = floors.get(i);
			if (floor.isEmpty()) continue;
			if (floor.getStatus() == Status.COMPLETE) continue;
			if (floor.getStatus() == Status.PLANNING) {
				if (justCheck) return i;
				BuilderWithInfo builder = new BuilderWithInfo(world, floor, size, high, pos);
				BuildProgress progress = new BuildProgress(floor.getType(), builder);
				floor.startProgress(progress);
				return i;
			}
			if (floor.getStatus() == Status.CONSTRUCTING) {
				BuildProgress progress = floor.getProgress();
				if (progress == null) {
					BuilderWithInfo builder = new BuilderWithInfo(world, floor, size, high, pos);
					progress = new BuildProgress(floor.getType(), builder);
					floor.startProgress(progress);
				}
				if (progress.needPeople()) {
					if (justCheck) return i;
					progress.markPeople();
					return i;
				}
				maybe = i;
			}
		}
		return maybe;
	}

	/** 根据申请的id开始建造建筑 */
	@Nullable
	public BuildProgress getBuildTask(int floorn) {
		if (floorn < 0 || floorn >= floors.size()) return null;
		FloorInfo floor = floors.get(floorn);
		return floor.getProgress();
	}

	/** 通知完成 */
	public void notifyComplete(int floorn) {
		if (floorn < 0 || floorn >= floors.size()) return;
		FloorInfo floor = floors.get(floorn);
		floor.setStatus(Status.COMPLETE);
		if (floor.isEmpty()) return;
		BuilderWithInfo builder = new BuilderWithInfo(world, floor, size, high, pos);
		floor.getType().surprise(builder, rand);
		floor.getType().spawn(builder);
		this.updateToClient();
		this.markDirty();
	}

	/**
	 * 安排一层
	 * 
	 * @param floor 本层的建筑类型
	 * @return 是否安排成功
	 * 
	 */
	public boolean scheduleFloor(ElfEdificeFloor floor) {
		if (floor == null) return false;
		int floorHigh = getFloorHigh(floors.size() - 1);
		FloorInfo info = new FloorInfo(floor, getTreeBasicPos().up(floorHigh + 1));
		BuilderWithInfo builder = new BuilderWithInfo(world, info, size, high, pos);
		info.setFloorData(info.getType().getBuildData(builder, RandomHelper.rand));
		info.setHigh((short) info.getType().getFloorHeight(builder));
		if (floorHigh + info.getHigh() >= high - 1) return false;
		if (info.getHigh() < 2) return false;
		floors.add(info);
		this.markDirty();
		return true;
	}

	// ----------------------------精灵大厦刷精灵部分----------------------------

	/** 尝试生成精灵 */
	public void trySpawnElfAround() {
		BlockPos pos = findAroundPlaceCanSpawn();
		if (pos == null) return;
		int searchRange = 16;
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - searchRange, pos.getY() - searchRange,
				pos.getZ() - searchRange, pos.getX() + searchRange, pos.getY() + searchRange, pos.getZ() + searchRange);
		List<EntityElfBase> elfs = world.getEntitiesWithinAABB(EntityElfBase.class, aabb);
		if (!elfs.isEmpty()) return;
		// 刷一只精灵
		EntityElf elf;
		switch (rand.nextInt(5)) {
		case 0:
			elf = new EntityElf(world, ElfProfession.BUILDER);
			break;
		default:
			if (this.floors.isEmpty() && rand.nextInt(3) == 0) elf = new EntityElf(world, ElfProfession.BUILDER);
			else elf = new EntityElf(world);
		}
		elf.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		elf.setEdificeCore(this.pos);
		world.spawnEntity(elf);
	}

	/** 寻找一个可以生成精灵的位置 */
	@Nullable
	public BlockPos findAroundPlaceCanSpawn() {
		int rx = rand.nextInt(size) + size + 1;
		int rz = rand.nextInt(size) + size + 1;
		if (rand.nextBoolean()) rx = -rx;
		if (rand.nextBoolean()) rz = -rz;
		int ry = rand.nextInt(4) + 4;
		BlockPos pos = this.pos.add(rx, -high + ry, rz);
		if (!world.isAirBlock(pos)) return null;
		while (world.isAirBlock(pos) && pos.getY() > 0) pos = pos.down();
		if (!world.getBlockState(pos).isFullBlock()) return null;
		return pos.up();
	}

	// ----------------------------精灵大厦电梯部分----------------------------

	/** 检查一个实体是不是位于电梯范围内 */
	public static boolean inRangeElevator(BlockPos corePos, Entity entity) {
		if (entity.posY >= corePos.getY()) return false;
		if (Math.abs(corePos.getX() - entity.posX) > 3) return false;
		if (Math.abs(corePos.getZ() - entity.posZ) > 3) return false;
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void openElevatorUI(BlockPos corePos, EntityPlayer player) {
		TileElfTreeCore core = BlockHelper.getTileEntity(player.world, corePos, TileElfTreeCore.class);
		if (core == null) return;
		if (!inRangeElevator(corePos, player)) return;
		player.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ELF_TREE_ELEVATOR, player.world, corePos.getX(),
				corePos.getY(), corePos.getZ());
	}

	public static void moveEntity(BlockPos corePos, Entity entity, int floor) {
		TileElfTreeCore core = BlockHelper.getTileEntity(entity.world, corePos, TileElfTreeCore.class);
		if (core != null) core.moveEntity(entity, floor);
	}

	public void moveEntity(Entity entity, int floor) {
		if (!inRangeElevator(pos, entity)) return;
		int high = this.getFloorHigh(floor - 1);
		BlockPos floorPos = this.getTreeBasicPos().add(0, high + 1, 0);
		Vec3d pos = new Vec3d(entity.posX, floorPos.getY(), entity.posZ);
		entity.setPositionAndUpdate(pos.x, pos.y, pos.z);
		world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F,
				1.0F);
	}

}