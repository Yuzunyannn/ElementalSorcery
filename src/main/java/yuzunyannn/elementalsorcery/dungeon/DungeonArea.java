package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class DungeonArea extends WorldSavedData {

	public static final int CHUNK_SPLIT = 4;

	public static class AreaExcerpt implements INBTSerializable<NBTTagCompound> {

		protected int minAX, minAZ, maxAX, maxAZ;
		protected int id;

		public AreaExcerpt() {

		}

		public AreaExcerpt(NBTTagCompound nbt) {
			this.deserializeNBT(nbt);
		}

		public ChunkPos getChunckPos() {
			return new ChunkPos((maxAX + minAX) / 2 * CHUNK_SPLIT, (maxAZ + minAZ) / 2 * CHUNK_SPLIT);
		}

		public boolean isInArea(ChunkPos pos) {
			int x = pos.x / CHUNK_SPLIT;
			int z = pos.z / CHUNK_SPLIT;
			return x >= minAX && x <= maxAX && z >= minAZ && z <= maxAZ;
		}

		public boolean isInArea(BlockPos pos) {
			return this.isInArea(new ChunkPos(pos));
		}

		public void append(ChunkPos pos) {
			minAX = Math.min(pos.x / CHUNK_SPLIT, minAX);
			minAZ = Math.min(pos.z / CHUNK_SPLIT, minAZ);
			maxAX = Math.max(pos.x / CHUNK_SPLIT, maxAX);
			maxAZ = Math.max(pos.z / CHUNK_SPLIT, maxAZ);
		}

		public void append(AxisAlignedBB box) {
			BlockPos minAt = new BlockPos(box.minX, box.minY, box.minZ);
			BlockPos maxAt = new BlockPos(box.maxX, box.maxY, box.maxZ);
			ChunkPos posMin = new ChunkPos(minAt);
			ChunkPos posMax = new ChunkPos(maxAt);
			for (int x = posMin.x; x <= posMax.x; x++) {
				for (int z = posMin.z; z <= posMax.z; z++) {
					ChunkPos pos = new ChunkPos(x, z);
					append(pos);
				}
			}
		}

		public void set(ChunkPos pos) {
			minAX = pos.x / CHUNK_SPLIT;
			minAZ = pos.z / CHUNK_SPLIT;
			maxAX = pos.x / CHUNK_SPLIT;
			maxAZ = pos.z / CHUNK_SPLIT;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("mix", minAX);
			nbt.setInteger("miz", minAZ);
			nbt.setInteger("max", maxAX);
			nbt.setInteger("maz", maxAZ);
			nbt.setInteger("id", id);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			minAX = nbt.getInteger("mix");
			minAZ = nbt.getInteger("miz");
			maxAX = nbt.getInteger("max");
			maxAZ = nbt.getInteger("maz");
			id = nbt.getInteger("id");
		}

	}

	protected final AreaExcerpt excerpt = new AreaExcerpt();
	protected String failMsg;
	protected List<DungeonAreaRoom> rooms = new ArrayList<>();
	protected int id;

	public DungeonArea(String name) {
		super(name);
	}

	public void generate(DungeonWorld dw, BlockPos at) {
		rooms.clear();
		
		DungeonRoomSelector selector = DungeonRoomSelector.create(this);

		DungeonAreaGenerater generate = new DungeonAreaGenerater(this, dw.world);
		DungeonAreaRoom room = new DungeonAreaRoom(selector.getFirstRoom());
		generate.addRoom(at, room);

		generate.addWaitBuildRoom(room);

		while (true) {
			room = generate.popWaitBuildRoom();
			if (room == null) break;
			generate.buildRoom(room, selector);
		}
		
		generate.checkRooms();

		return;
	}

	protected DungeonArea setFailMsg(String failMsg) {
		this.failMsg = failMsg;
		return this;
	}

	public String getFailMsg() {
		return failMsg;
	}

	public boolean isFail() {
		return failMsg != null;
	}

	public AreaExcerpt getExcerpt() {
		return excerpt;
	}

	public DungeonAreaRoom getRoomById(int id) {
		return id < 0 || id >= rooms.size() ? null : rooms.get(id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		excerpt.deserializeNBT(nbt.getCompoundTag("e"));
		rooms = NBTHelper.getNBTSerializableList(nbt, "rooms", DungeonAreaRoom.class, NBTTagCompound.class);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("e", excerpt.serializeNBT());
		NBTHelper.setNBTSerializableList(compound, "rooms", rooms);
		return compound;
	}

	public void debugBuildDungeon(World world) {
		for (DungeonAreaRoom room : rooms) room.inst.build(world, this, room);
	}
}
