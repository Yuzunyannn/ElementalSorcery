package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSummon;
import yuzunyannn.elementalsorcery.summon.recipe.SummonRecipe;
import yuzunyannn.elementalsorcery.summon.recipe.SummonRecipeDungeonRoom;
import yuzunyannn.elementalsorcery.util.TextHelper;
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

		public int getId() {
			return id;
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

	public DungeonArea(String name) {
		super(name);
	}

	public void generate(DungeonWorld dw, BlockPos at) {
		rooms.clear();

		DungeonRoomSelector selector = DungeonRoomSelector.create(this);

		DungeonAreaGenerator generate = new DungeonAreaGenerator(this, dw.world);
		DungeonAreaRoom room = new DungeonAreaRoom(selector.getFirstRoom());
		room.facing = EnumFacing.HORIZONTALS[generate.rand.nextInt(EnumFacing.HORIZONTALS.length)];
		generate.addRoom(at, room);

		generate.addWaitBuildRoom(room);

		while (true) {
			room = generate.popWaitBuildRoom();
			if (room == null) break;
			generate.buildRoom(room, selector);
		}

		generate.checkRooms();
		ESAPI.logger.info("建造完成，共建造了:" + selector);

		this.markDirty();
	}

	@Nullable
	public DungeonAreaRoom findRoom(Vec3d vec) {
		for (DungeonAreaRoom room : rooms) {
			AxisAlignedBB aabb = room.getBox();
			if (aabb.contains(vec)) return room;
		}
		return null;
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

	public void startBuildRoom(World world, int roomId, EntityPlayer openPlayer) {
		DungeonAreaRoom room = getRoomById(roomId);
		if (room == null) return;
		if (room.isBuild) return;

		room.isBuild = true;

		int areaId = this.excerpt.id;

		MantraSummon.summon(world, room.at, openPlayer,
				SummonRecipeDungeonRoom.createVestKeepsake(areaId, roomId, openPlayer),
				SummonRecipe.get(TextHelper.toESResourceLocation("dungeon_room")));

		this.markDirty();
	}

	public void startOpenDoor(World world, int roomId, int doorIndex) {
		DungeonAreaRoom room = getRoomById(roomId);
		if (room == null) return;
		if (!room.isBuild) return;

		DungeonAreaDoor door = room.getDoorLink(doorIndex);
		if (door == null) return;
		if (door.isOpen()) return;

		door.isOpen = true;

		DungeonRoomType inst = room.inst;
		DungeonRoomDoor iDoor = inst.getDoors().get(doorIndex);

		AxisAlignedBB box = iDoor.getDoorBox(iDoor.getDoorLenghToBorder(inst.getBuildingBox()));
		box = BuildingFace.face(box, room.facing).offset(room.at);

		int minX = MathHelper.ceil(box.minX);
		int maxX = MathHelper.ceil(box.maxX);
		int minY = MathHelper.ceil(box.minY);
		int maxY = MathHelper.ceil(box.maxY);
		int minZ = MathHelper.ceil(box.minZ);
		int maxZ = MathHelper.ceil(box.maxZ);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos at = new BlockPos(x, y, z);
					world.destroyBlock(at, false);
				}
			}
		}

		this.markDirty();
	}

	public void onRoomBuildFinish(World world, DungeonAreaRoom room) {
		List<DungeonAreaDoor> doors = room.getDoorLinks();
		for (int doorIndex = 0; doorIndex < doors.size(); doorIndex++) {
			DungeonAreaDoor door = doors.get(doorIndex);
			if (!door.isLink()) continue;
			DungeonAreaRoom otherRoom = this.getRoomById(door.getLinkRoomId());
			if (!otherRoom.isBuild) continue;
			int otherDoorIndex = door.getLinkDoorIndex();
			this.startOpenDoor(world, room.id, doorIndex);
			this.startOpenDoor(world, otherRoom.id, otherDoorIndex);
		}
	}

	public void debugBuildDungeon(World world) {
		for (DungeonAreaRoom room : rooms) room.inst.deubgBuild(world, this, room);
	}
}
