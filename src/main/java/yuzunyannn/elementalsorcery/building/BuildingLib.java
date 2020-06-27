package yuzunyannn.elementalsorcery.building;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageGetBuilingInfo;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class BuildingLib {

	public static BuildingLib instance = new BuildingLib();

	private Map<String, Building> mapClient = new HashMap<String, Building>();
	private Map<String, BuildingSaveData> mapSave = new HashMap<String, BuildingSaveData>();
	private Map<String, Building> mapLib = new HashMap<String, Building>();
	long lastCheckTime = System.currentTimeMillis();

	public Collection<Building> getBuildingsFromLib() {
		return mapLib.values();
	}

	/** 添加建筑到lib */
	void addBuildingLib(String key, Building building) {
		if (mapLib.containsKey(key)) throw new IllegalArgumentException("The key has already exist!");
		building.setKeyName(key);
		mapLib.put(key, building);
	}

	/** 添加建筑到建筑存储 */
	void addBuilding(BuildingSaveData data) {
		String key = data.building.getKeyName();
		if (mapSave.containsKey(key)) {
			ElementalSorcery.logger.warn("The key has already exist!");
			return;
		}
		mapSave.put(key, data);
	}

	/** 添加建筑到建筑存储 */
	public String addBuilding(Building building) {
		BuildingSaveData data = new BuildingSaveData(building);
		this.addBuilding(data);
		return data.building.getKeyName();
	}

	/** 获取建筑 */
	public Building getBuilding(String key) {
		if (mapLib.containsKey(key)) return mapLib.get(key);
		if (mapSave.containsKey(key)) return mapSave.get(key).building;
		return null;
	}

	@SideOnly(Side.CLIENT)
	public Building giveBuilding(String key) {
		Building building = this.getBuilding(key);
		if (building != null) return building;
		if (mapClient.containsKey(key)) { return mapClient.get(key); }
		mapClient.put(key, new Building());
		return mapClient.get(key);
	}

	@SideOnly(Side.CLIENT)
	public void synBuilding(String key, NBTTagCompound nbt) {
		Building building = this.giveBuilding(key);
		building.deserializeNBT(nbt);
		building.setKeyName(key);
	}

	@SideOnly(Side.CLIENT)
	static public void wantBuildingDatasFormServer(String key) {
		ESNetwork.instance.sendToServer(new MessageGetBuilingInfo(key));
	}

	/** 使用某个建筑，让建筑的时间更新 */
	void use(String key) {
		if (mapSave.containsKey(key)) {
			BuildingSaveData data = mapSave.get(key);
			data.use(this.lastCheckTime);
		}
	}

	// 创建默认名字
	/*
	 * public static final String SPELLBOOK_ALTAR = "spellbook_altar"; public static
	 * final String LARGE_ALTAR = "large_altar"; public static final String
	 * ELEMENT_CRAFTING_ALTAR = "element_crafting_altar"; public static final String
	 * DECONSTRUCT_ALTAR = "deconstruct_altar"; public static final String
	 * BUILDING_ALTAR = "building_altar"; public static final String ANALYSIS_ALTAR
	 * = "analysis_altar"; public static final String INFUSION = "infusion"; public
	 * static final String ABSORB_BOX = "absorb_box"; public static final String
	 * DECONSTRUCT_BOX = "deconstruct_box";
	 */

	public static void registerAll() throws IOException {
		final ESData data = ElementalSorcery.data;
		final String MODID = ElementalSorcery.MODID;
		String[] mapJsonNames = data.getFilesFromResource(new ResourceLocation(MODID, "structures"));
		for (String path : mapJsonNames) {
			NBTTagCompound nbt = data.getNBTFromResource(new ResourceLocation(MODID, "structures/" + path));
			if (path.lastIndexOf('.') != -1) path = path.substring(0, path.lastIndexOf('.'));
			instance.addBuildingLib(path, new BuildingInherent(nbt, TextHelper.castToCamel(path)));
		}
		Buildings.init();
		BuildingLib.loadBuilding();
	}

	/** 初始化加载 */
	private static void loadBuilding() {
		File file = ElementalSorcery.data.getFile("building/tmp", "");
		File[] files = file.listFiles();
		for (File f : files) {
			try {
				BuildingSaveData data = new BuildingSaveData(f);
				BuildingLib.instance.addBuilding(data);
			} catch (IOException e) {
				ElementalSorcery.logger.warn("无效的建筑文件：" + f + "--处理：尝试删除！");
				f.delete();
			}
		}
	}

	/** 处理文件信息 */
	public void dealSave() {
		lastCheckTime = System.currentTimeMillis();
		LinkedList<String> removeList = new LinkedList<String>();
		for (Entry<String, BuildingSaveData> entry : mapSave.entrySet()) {
			BuildingSaveData data = entry.getValue();
			boolean out = data.deal(lastCheckTime);
			if (out == false) {
				removeList.add(entry.getKey());
			}
		}
		for (String remove : removeList) {
			this.mapSave.remove(remove);
		}
	}

}
