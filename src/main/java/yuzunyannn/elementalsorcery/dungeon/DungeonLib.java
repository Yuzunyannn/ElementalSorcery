package yuzunyannn.elementalsorcery.dungeon;

import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.building.BuildingLib;

public class DungeonLib {

	public static DungeonRoomType DUNGEON_CENTER;
	public static DungeonRoomType DUNGEON_SMALL_TOWARD4;
	public static DungeonRoomType DUNGEON_CORRIDOR_TOWARD4;
	public static DungeonRoomType DUNGEON_SMAL_PRISON_TOWARD2;
	public static DungeonRoomType DUNGEON_SMALL_LIBRARY_TOWARD3;
	public static DungeonRoomType DUNGEON_SMALL_ROOM_TOWARD1;
	public static DungeonRoomType DUNGEON_MANTRA_LAB_TOWARD2;
	public static DungeonRoomType DUNGEON_ROOM_TOWARD2;
	public static DungeonRoomType DUNGEON_SMALL_GARDEN_TOWARD3;
	public static DungeonRoomType DUNGEON_GREENHOUSE_TOWARD4;
	public static DungeonRoomType DUNGEON_STRATEGY_HALL_TOWARD3;
	public static DungeonRoomType DUNGEON_CHECKPOINT;
	public static DungeonRoomType DUNGEON_LOOKOUT_TOWER_TOWARD4;
	public static DungeonRoomType DUNGEON_SHADY_PARK_TOWARD4;
	public static DungeonRoomType DUNGEON_LABORATORY_TOWARD2;
	public static DungeonRoomType DUNGEON_SHADY_PATH_TOWARD2;
	public static DungeonRoomType DUNGEON_CLINIC_TOWARD2;
	public static DungeonRoomType DUNGEON_RESTAURANT_TOWARD2;
	public static DungeonRoomType DUNGEON_STREET_TOWARD5;
	public static DungeonRoomType DUNGEON_BURNER_TOWARD2;
	public static DungeonRoomType DUNGEON_BIOLOGY_LAB_TOWARD2;
	public static DungeonRoomType DUNGEON_POWER_PLANT_TOWARD2;
	public static DungeonRoomType DUNGEON_SATELLITE_STATION_TOWARD3;
	public static DungeonRoomType DUNGEON_PRISON_TOWARD3;
	public static DungeonRoomType DUNGEON_LIBRARY_TOWARD3;
	public static DungeonRoomType DUNGEON_ROOM_TOWARD3;
	public static DungeonRoomType DUNGEON_TOWARD4;
	public static DungeonRoomType DUNGEON_GUARD_FACTORY_TOWARD4;
	public static DungeonRoomType DUNGEON_CALAMITY_EDIFICE_TOWARD4;
	public static DungeonRoomType DUNGEON_INVERT_LAB_TOWARD2 ;
	public static DungeonRoomType DUNGEON_METEORITE_CRATER_TOWARD1;
	public static DungeonRoomType DUNGEON_POSTERN_TOWARD1;

	public static void register(DungeonRoomType room) {
		DungeonRoomType.REGISTRY.register(room);
	}

	private static DungeonRoomType register(String id) {
		DungeonRoomType room = new DungeonRoomType(BuildingLib.instance.getBuilding(id));
		room.setRegistryName(ESAPI.MODID, room.getStructure().getKeyName());
		room.postInit();
		DungeonRoomType.REGISTRY.register(room);
		return room;
	}

	public static void registerAll() {
		registerAllFunc();
		DUNGEON_CENTER = register("dungeon_center");
		DUNGEON_SMALL_TOWARD4 = register("dungeon_small_toward4");
		DUNGEON_CORRIDOR_TOWARD4 = register("dungeon_corridor_toward4");
		DUNGEON_SMAL_PRISON_TOWARD2 = register("dungeon_smal_prison_toward2");
		DUNGEON_SMALL_LIBRARY_TOWARD3 = register("dungeon_small_library_toward3");
		DUNGEON_SMALL_ROOM_TOWARD1 = register("dungeon_small_room_toward1");
		DUNGEON_MANTRA_LAB_TOWARD2 = register("dungeon_mantra_lab_toward2");
		DUNGEON_ROOM_TOWARD2 = register("dungeon_room_toward2");
		DUNGEON_SMALL_GARDEN_TOWARD3 = register("dungeon_small_garden_toward3");
		DUNGEON_GREENHOUSE_TOWARD4 = register("dungeon_greenhouse_toward4");
		DUNGEON_STRATEGY_HALL_TOWARD3 = register("dungeon_strategy_hall_toward3");
		DUNGEON_CHECKPOINT = register("dungeon_checkpoint");
		DUNGEON_LOOKOUT_TOWER_TOWARD4 = register("dungeon_lookout_tower_toward4");
		DUNGEON_SHADY_PARK_TOWARD4 = register("dungeon_shady_park_toward4");
		DUNGEON_LABORATORY_TOWARD2 = register("dungeon_laboratory_toward2");
		DUNGEON_SHADY_PATH_TOWARD2 = register("dungeon_shady_path_toward2");
		DUNGEON_CLINIC_TOWARD2 = register("dungeon_clinic_toward2");
		DUNGEON_RESTAURANT_TOWARD2 = register("dungeon_restaurant_toward2");
		DUNGEON_STREET_TOWARD5 = register("dungeon_street_toward5");
		DUNGEON_BURNER_TOWARD2 = register("dungeon_burner_toward2");
		DUNGEON_BIOLOGY_LAB_TOWARD2 = register("dungeon_biology_lab_toward2");
		DUNGEON_POWER_PLANT_TOWARD2 = register("dungeon_power_plant_toward2");
		DUNGEON_SATELLITE_STATION_TOWARD3 = register("dungeon_satellite_station_toward3");
		DUNGEON_PRISON_TOWARD3 = register("dungeon_prison_toward3");
		DUNGEON_LIBRARY_TOWARD3 = register("dungeon_library_toward3");
		DUNGEON_ROOM_TOWARD3 = register("dungeon_room_toward3");
		DUNGEON_TOWARD4 = register("dungeon_toward4");
		DUNGEON_GUARD_FACTORY_TOWARD4 = register("dungeon_guard_factory_toward4");
		DUNGEON_CALAMITY_EDIFICE_TOWARD4 = register("dungeon_calamity_edifice_toward4");
		DUNGEON_INVERT_LAB_TOWARD2 = register("dungeon_invert_lab_toward2");
		DUNGEON_METEORITE_CRATER_TOWARD1 = register("dungeon_meteorite_crater_toward1");
		DUNGEON_POSTERN_TOWARD1 = register("dungeon_postern_toward1");
	}

	public static void registerAllFunc() {
		// can common
		GameFunc.factoryMap.put("chest", DungeonFuncChest.class);
		GameFunc.factoryMap.put("entity", DungeonFuncEntity.class);
		GameFunc.factoryMap.put("loot", DungeonFuncLoot.class);
		GameFunc.factoryMap.put("explode", DungeonFuncExplode.class);
		GameFunc.factoryMap.put("haystack", DungeonFuncHaystack.class);
		GameFunc.factoryMap.put("magicCircle", DungeonFuncMagicCircle.class);
		GameFunc.factoryMap.put("potion", DungeonFuncPotion.class);
		GameFunc.factoryMap.put("mantra", DungeonFuncMantra.class);
		GameFunc.factoryMap.put("block", DungeonFuncBlock.class);
		// dungeon
		GameFunc.factoryMap.put("dungeon:global", DungeonFuncGlobal.class);
		GameFunc.factoryMap.put("dungeon:area", DungeonFuncArea.class);
	}

}
