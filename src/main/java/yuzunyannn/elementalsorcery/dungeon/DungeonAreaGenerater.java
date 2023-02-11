package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.building.BuildingFace;

public class DungeonAreaGenerater {

	public final DungeonArea self;
	public final World world;
	Map<ChunkPos, List<Integer>> roomMap = new HashMap<>();
	LinkedList<DungeonAreaRoom> waitQueue = new LinkedList<>();
	Random rand = new Random();
	int doorLinkGap = 3;

	public DungeonAreaGenerater(DungeonArea area, World world) {
		this.self = area;
		this.world = world;
	}

	public boolean isRoomOverlap(DungeonAreaRoom room) {
		AxisAlignedBB box = room.getBox();
		BlockPos minAt = new BlockPos(box.minX, box.minY, box.minZ);
		BlockPos maxAt = new BlockPos(box.maxX, box.maxY, box.maxZ);
		if (world.isOutsideBuildHeight(minAt)) return true;
		if (world.isOutsideBuildHeight(maxAt)) return true;
		ChunkPos posMin = new ChunkPos(minAt);
		ChunkPos posMax = new ChunkPos(maxAt);
		for (int x = posMin.x - 1; x <= posMax.x + 1; x++) {
			for (int z = posMin.z - 1; z <= posMax.z + 1; z++) {
				ChunkPos pos = new ChunkPos(x, z);
				if (checkOverlap(pos, box)) return true;
			}
		}
		return false;
	}

	public boolean checkOverlap(ChunkPos pos, AxisAlignedBB box) {
		List<Integer> list = roomMap.get(pos);
		if (list == null) return false;
		for (Integer id : list) {
			DungeonAreaRoom otherRoom = self.getRoomById(id);
			if (box.intersects(otherRoom.getBox())) return true;
		}
		return false;
	}

	public void addRoom(BlockPos at, DungeonAreaRoom room) {
		ChunkPos pos = new ChunkPos(at);
		room.id = self.rooms.size();
		room.at = at;
		room.areId = self.excerpt.id;
		self.rooms.add(room);

		AxisAlignedBB box = room.getBox();
		//加入list
		ChunkPos posMin = new ChunkPos(new BlockPos(box.minX, box.minY, box.minZ));
		ChunkPos posMax = new ChunkPos(new BlockPos(box.maxX, box.maxY, box.maxZ));
		for (int x = posMin.x; x <= posMax.x; x++) {
			for (int z = posMin.z; z <= posMax.z; z++) {
				pos = new ChunkPos(x, z);
				List<Integer> list = roomMap.get(pos);
				if (list == null) roomMap.put(pos, list = new LinkedList<>());
				list.add(room.id);
			}
		}

		if (room.id == 0) self.excerpt.set(pos);
		self.excerpt.append(box);
	}

	public void addWaitBuildRoom(DungeonAreaRoom room) {
		waitQueue.addLast(room);
	}

	public DungeonAreaRoom popWaitBuildRoom() {
		if (waitQueue.isEmpty()) return null;
		DungeonAreaRoom first = waitQueue.getFirst();
		waitQueue.removeFirst();
		return first;
	}

	static class DoorInfo {
		public int index;
		public int width, height;
		public AxisAlignedBB doorBox;
		public BlockPos doorBorderBottomPos;
		public EnumFacing orient;

		public DoorInfo(DungeonRoomType room, int doorIndex) {
			DungeonRoomDoor door = room.getDoors().get(doorIndex);
			int length = door.getDoorLenghToBorder(room.getBuildingBox());
			doorBorderBottomPos = door.getCorePos().offset(door.orient, length).down(door.expandDown);
			doorBox = door.getDoorBox(length);
			width = door.getDoorWidth();
			height = door.getDoorHeight();
			orient = door.orient;
			index = doorIndex;
		}

		public void face(EnumFacing facing) {
			orient = BuildingFace.face(orient, facing);
			doorBorderBottomPos = BuildingFace.face(doorBorderBottomPos, facing);
		}
	}

	public void buildRoom(DungeonAreaRoom room, DungeonRoomSelector selector) {
		List<DungeonRoomDoor> idoors = room.inst.getDoors();
		int randomStartDoorIndex = rand.nextInt(idoors.size());
		for (int i = 0; i < idoors.size(); i++) {
			int doorIndex = (i + randomStartDoorIndex) % idoors.size();
			// 获取门信息
			DoorInfo doorInfo = new DoorInfo(room.inst, doorIndex);
			doorInfo.face(room.facing);
			// 获取接下来可以构建的内容
			Collection<DungeonRoomType> alternateRooms = selector.getAlternateRooms(room, doorIndex);
			// 沒有了,走人
			if (alternateRooms == null || alternateRooms.isEmpty()) continue;
			// 有,尝试进行选择
			for (DungeonRoomType alternateRoomInst : alternateRooms) {
				List<DungeonAreaRoom> list = checkDoorMatch(alternateRoomInst, doorInfo, room);
				if (list.isEmpty()) continue;
				DungeonAreaRoom alternateRoom = list.get(rand.nextInt(list.size()));
				addRoom(alternateRoom.at, alternateRoom);
				addWaitBuildRoom(alternateRoom);
				makeLink(alternateRoom, doorInfo, room);
				selector.onBuildRoom(alternateRoom);
				break;
			}
		}
	}

	public void makeLink(DungeonAreaRoom alternateRoom, DoorInfo doorInfo, DungeonAreaRoom parent) {
		for (int doorIndex = 0; doorIndex < alternateRoom.doorLinks.size(); doorIndex++) {
			DungeonAreaDoor adoor = alternateRoom.doorLinks.get(doorIndex);
			if (!adoor.isLink()) continue;
			if (adoor.getLinkRoomId() != parent.id) throw new RuntimeException("算法错了");
			parent.doorLinks.get(doorInfo.index).setLink(alternateRoom.id, doorIndex);
		}
	}

	public List<DungeonAreaRoom> checkDoorMatch(DungeonRoomType alternateRoomInst, DoorInfo doorInfo,
			DungeonAreaRoom parent) {
		List<DungeonRoomDoor> idoors = alternateRoomInst.getDoors();

		EnumFacing needDoorOrient = doorInfo.orient.getOpposite();
		BlockPos nextDoorPos = doorInfo.doorBorderBottomPos.offset(doorInfo.orient);

		int currBadFactor = Integer.MAX_VALUE;
		List<DungeonAreaRoom> currSelectedList = new ArrayList<>(idoors.size());

		for (int doorIndex = 0; doorIndex < idoors.size(); doorIndex++) {

			int badFactor = 0;

			DungeonAreaRoom alternateRroom = new DungeonAreaRoom(alternateRoomInst);
			DungeonAreaDoor adoor = alternateRroom.doorLinks.get(doorIndex);

			DoorInfo alternateDoorInfo = new DoorInfo(alternateRoomInst, doorIndex);
			badFactor += Math.abs(alternateDoorInfo.width - doorInfo.width) * 10;
			badFactor += Math.abs(alternateDoorInfo.height - doorInfo.height) * 5;

			Rotation roomRotation = BuildingFace.getRoation(alternateDoorInfo.orient, needDoorOrient);
			alternateRroom.facing = BuildingFace.fromRotation(roomRotation);
			alternateDoorInfo.face(alternateRroom.facing);

			BlockPos alternateCenterPos = nextDoorPos.subtract(alternateDoorInfo.doorBorderBottomPos);
			alternateRroom.at = parent.at.add(alternateCenterPos);

			if (isRoomOverlap(alternateRroom)) continue;

			if (badFactor < currBadFactor) {
				currBadFactor = badFactor;
				currSelectedList.clear();
			}

			adoor.setLink(parent.id, doorInfo.index);
			currSelectedList.add(alternateRroom);
		}

		return currSelectedList;
	}

	public void checkRooms() {
		for (DungeonAreaRoom room : self.rooms) {
			for (int doorIndex = 0; doorIndex < room.doorLinks.size(); doorIndex++) {
				DungeonAreaDoor door = room.doorLinks.get(doorIndex);
				if (door.isLink()) continue;
				checkRoomCanLinked(room, doorIndex);
			}
		}
	}

	// 如果门对着，自动连接上
	public void checkRoomCanLinked(DungeonAreaRoom room, int doorIndex) {
		AxisAlignedBB box = room.getBox();
		ChunkPos posMin = new ChunkPos(new BlockPos(box.minX, box.minY, box.minZ));
		ChunkPos posMax = new ChunkPos(new BlockPos(box.maxX, box.maxY, box.maxZ));
		for (int x = posMin.x - 1; x <= posMax.x + 1; x++) {
			for (int z = posMin.z - 1; z <= posMax.z + 1; z++) {
				ChunkPos pos = new ChunkPos(x, z);
				List<Integer> list = roomMap.get(pos);
				if (list == null) continue;
				for (Integer id : list) {
					if (id == room.id) continue;
					DungeonAreaRoom otherRoom = self.getRoomById(id);
					checkRoomCanLinked(room, doorIndex, otherRoom);
				}
			}
		}
	}

	public void checkRoomCanLinked(DungeonAreaRoom room, int doorIndex, DungeonAreaRoom otherRoom) {
		for (int otherDoorIndex = 0; otherDoorIndex < otherRoom.doorLinks.size(); otherDoorIndex++) {
			DungeonAreaDoor door = room.doorLinks.get(doorIndex);
			DungeonAreaDoor otherDoor = otherRoom.doorLinks.get(otherDoorIndex);
			if (otherDoor.isLink()) continue;

			DungeonRoomDoor roomDoor = room.inst.getDoors().get(doorIndex);
			DungeonRoomDoor otherRoomDoor = otherRoom.inst.getDoors().get(otherDoorIndex);

			EnumFacing doorFacing = BuildingFace.face(roomDoor.orient, room.facing);
			EnumFacing otherDoorFacing = BuildingFace.face(otherRoomDoor.orient, otherRoom.facing);

			// 必须是正对着
			if (doorFacing.getOpposite() != otherDoorFacing) continue;

			int length = roomDoor.getDoorLenghToBorder(room.inst.getBuildingBox());
			int otherLength = otherRoomDoor.getDoorLenghToBorder(otherRoom.inst.getBuildingBox());

			AxisAlignedBB box = roomDoor.getDoorBox(length + doorLinkGap);
			AxisAlignedBB otherBox = otherRoomDoor.getDoorBox(otherLength + doorLinkGap);
			box = box.expand(0.1, 0.1, 0.1).expand(-0.1, -0.1, -0.1);
			otherBox = otherBox.expand(0.1, 0.1, 0.1).expand(-0.1, -0.1, -0.1);

			box = BuildingFace.face(box, room.facing).offset(room.at);
			otherBox = BuildingFace.face(otherBox, otherRoom.facing).offset(otherRoom.at);

			if (!box.intersects(otherBox)) continue;

			door.setLink(otherRoom.id, otherDoorIndex);
			otherDoor.setLink(room.id, doorIndex);

			break;
		}
	}

}
