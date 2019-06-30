package yuzunyan.elementalsorcery.building;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import yuzunyan.elementalsorcery.ElementalSorcery;

public class BuildingLib {

	public static BuildingLib instance = new BuildingLib();

	private Map<String, Building> map = new HashMap<String, Building>();
	private Map<String, BuildingSaveData> mapSave = new HashMap<String, BuildingSaveData>();

	public Collection<Building> getBuildings() {
		return map.values();
	}

	void addBuilding(String name, Building building) {
		if (map.containsKey(name))
			throw new IllegalArgumentException("The name has already exist!");
		building.setKeyName(name);
		map.put(name, building);
	}

	void addBuilding(String name, Building building, BuildingSaveData data) {
		if (map.containsKey(name))
			throw new IllegalArgumentException("The name has already exist!");
		building.setKeyName(name);
		map.put(name, building);
		mapSave.put(name, data);
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

	public static void registerAll() {
		Buildings.init();
		instance.addBuilding(LARGE_ALTAR, Buildings.LARGE_ALTAR);
		instance.addBuilding(SPELLBOOK_ALTAR, Buildings.SPELLBOOK_ALTAR);
		instance.addBuilding(ELEMENT_CRAFTING_ALTAR, Buildings.ELEMENT_CRAFTING_ALTAR);
		instance.addBuilding(DECONSTRUCT_ALTAR, Buildings.DECONSTRUCT_ALTAR);
		BuildingLib.loadBuilding();
	}

	public static String addBuildingToLib(Building building) {
		BuildingSaveData data = new BuildingSaveData(building);
		BuildingLib.addData(data);
		return data.building.getKeyName();
	}

	private static void loadBuilding() {
		File file = ElementalSorcery.data.getESFile("building/tmp", "");
		File[] files = file.listFiles();
		for (File f : files) {
			try {
				BuildingSaveData data = new BuildingSaveData(f);
				addData(data);
			} catch (IOException e) {
				ElementalSorcery.logger.warn("无效的建筑文件：" + f + "--处理：尝试删除！");
				f.delete();
			}
		}
	}

	private static void addData(BuildingSaveData data) {
		String key = data.building.getKeyName();
		try {
			BuildingLib.instance.addBuilding(key, data.building, data);
		} catch (IllegalArgumentException e) {
			ElementalSorcery.logger.warn("已经存在id为(" + key + ")的building");
		}
	}

	long lastCheckTime;

	/** 处理文件信息 */
	public void deal() {
		lastCheckTime = System.currentTimeMillis();
		LinkedList<String> removeList = new LinkedList<String>();
		for (Entry<String, BuildingSaveData> entry : mapSave.entrySet()) {
			BuildingSaveData data = entry.getValue();
			boolean out = data.deal(lastCheckTime);
			if (out) {
				removeList.add(entry.getKey());
			}
		}
		for (String remove : removeList) {
			this.map.remove(remove);
			this.mapSave.remove(remove);
		}
	}

}
