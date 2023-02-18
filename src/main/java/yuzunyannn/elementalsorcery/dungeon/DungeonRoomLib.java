package yuzunyannn.elementalsorcery.dungeon;

import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.building.BuildingLib;

public class DungeonRoomLib {

	public static DungeonRoomType DUNGEON_CENTER;
	public static DungeonRoomType DUNGEON_SMALL_TOWARD4;
	public static DungeonRoomType DUNGEON_CORRIDOR_TOWARD4;

	public static void register(DungeonRoomType room) {
		DungeonRoomType.REGISTRY.register(room);
	}

	private static DungeonRoomType register(String id) {
		DungeonRoomType room = new DungeonRoomType(BuildingLib.instance.getBuilding(id));
		room.setRegistryName(ESAPI.MODID, room.getStructure().getKeyName());
		DungeonRoomType.REGISTRY.register(room);
		return room;
	}

	public static void registerAll() {
		DUNGEON_CENTER = register("dungeon_center");
		DUNGEON_SMALL_TOWARD4 = register("dungeon_small_toward4");
		DUNGEON_CORRIDOR_TOWARD4 = register("dungeon_corridor_toward4");
	}

}
