package yuzunyannn.elementalsorcery.dungeon;

import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.building.BuildingLib;

public class DungeonLib {

	public static DungeonRoomType DUNGEON_CENTER;
	public static DungeonRoomType DUNGEON_SMALL_TOWARD4;
	public static DungeonRoomType DUNGEON_CORRIDOR_TOWARD4;
	public static DungeonRoomType DUNGEON_SMAL_PRISON_TOWARD2;

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
		registerAllFunc();
		DUNGEON_CENTER = register("dungeon_center");
		DUNGEON_SMALL_TOWARD4 = register("dungeon_small_toward4");
		DUNGEON_CORRIDOR_TOWARD4 = register("dungeon_corridor_toward4");
		DUNGEON_SMAL_PRISON_TOWARD2 = register("dungeon_smal_prison_toward2");
	}

	public static void registerAllFunc() {
		GameFunc.factoryMap.put("global", DungeonFuncGlobal.class);
		GameFunc.factoryMap.put("chest", GameFuncChest.class);
		GameFunc.factoryMap.put("entity", GameFuncEntity.class);
		GameFunc.factoryMap.put("loot", GameFuncLoot.class);
		GameFunc.factoryMap.put("haystack", DungeonFuncHaystack.class);
	}

}
