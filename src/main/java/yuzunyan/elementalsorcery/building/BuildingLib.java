package yuzunyan.elementalsorcery.building;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BuildingLib {

	public static BuildingLib instance = new BuildingLib();

	private Map<String, Building> map = new HashMap<String, Building>();

	public Collection<Building> getBuildings() {
		return map.values();
	}

	public void addBuilding(String name, Building building) {
		if (map.containsKey(name))
			throw new IllegalArgumentException("The name has already exist!");
		building.setKeyName(name);
		map.put(name, building);
	}

	public Building getBuilding(String name) {
		if (map.containsKey(name))
			return map.get(name);
		return null;
	}

	public Set<String> getBuildingsName() {
		return map.keySet();
	}

	// 创建默认名字
	public static final String SPELLBOOK_ALTAR = "spellbook_altar";
	public static final String LARGE_ALTAR = "large_altar";
	public static final String ELEMENT_CRAFTING_ALTAR = "element_crafting_altar";
	public static final String DECONSTRUCT_ALTAR = "deconstruct_altar";

	public static void addBuildingToLib(String key, Building building) {
		instance.addBuilding(key, building.setKeyName(key));
	}

	public static void registerAll() {
		Buildings.init();
		addBuildingToLib(LARGE_ALTAR, Buildings.LARGE_ALTAR);
		addBuildingToLib(SPELLBOOK_ALTAR, Buildings.SPELLBOOK_ALTAR);
		addBuildingToLib(ELEMENT_CRAFTING_ALTAR, Buildings.ELEMENT_CRAFTING_ALTAR);
		addBuildingToLib(DECONSTRUCT_ALTAR, Buildings.DECONSTRUCT_ALTAR);
	}
}
