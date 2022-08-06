package yuzunyannn.elementalsorcery.building;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageGetBuilingInfo;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.IOHelper;

public class BuildingLib {

	@Config
	@Config.NumberRange(min = 0, max = 60 * 24 * 365 * 100)
	public static int BUILDING_MAX_REMAIN_TIMES = 60 * 24;

	public static final BuildingLib instance = new BuildingLib();

	private Map<String, Building> mapClient = new HashMap<String, Building>();
	private Map<String, Building> mapLib = new HashMap<String, Building>();
	/**
	 * mapClient只有客户端用，mapLib一旦加载完就是定死的，不应该修改<br/>
	 * 为了节省内存，单人模式下mapClient没有作用 <br/>
	 * 所以只有这个保存的缓存数据，会在服务器线程和客户端线程同时添加改查
	 */
	private Map<String, BuildingSaveData> mapSave = new ConcurrentHashMap<String, BuildingSaveData>();

	long lastCheckTime = System.currentTimeMillis();

	public Collection<Building> getBuildingsFromLib() {
		return mapLib.values();
	}

	/** 添加建筑到lib */
	void addBuildingLib(String key, Building building) {
		if (mapLib.containsKey(key)) throw new IllegalArgumentException("The key(" + key + ") has already exist!");
		building.setKeyName(key);
		mapLib.put(key, building);
	}

	/** 添加建筑到建筑存储 */
	private void addBuilding(BuildingSaveData data) {
		String key = data.building.getKeyName();
		if (mapSave.containsKey(key)) {
			ESAPI.logger.warn("The key(" + key + ") has already exist!");
			return;
		}
		mapSave.put(key, data);
	}

	/**
	 * 添加建筑到建筑存储
	 * 
	 * @param isTemp 是否进入临时的建筑，临时建筑会随机key且以$开头，非临时建筑会在没有key时随机
	 * 
	 */
	public String addBuilding(Building building, boolean isTemp) {
		BuildingSaveData data = isTemp ? new BuildingSaveData(building) : new BuildingSaveDataJson(building);
		this.addBuilding(data);
		return data.building.getKeyName();
	}

	/** 获取建筑 */
	public Building getBuilding(String key) {
		if (mapLib.containsKey(key)) return mapLib.get(key);
		if (mapSave.containsKey(key)) return mapSave.get(key).building;
		BuildingSaveData data = loadBuildingFromTemp(key);
		if (data != null) return data.building;
		return null;
	}

	@SideOnly(Side.CLIENT)
	public Building giveBuilding(String key) {
		if (mapLib.containsKey(key)) return mapLib.get(key);
		if (mapClient.containsKey(key)) return mapClient.get(key);
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

	/** 当使用某个建筑，或者对其进行修改，让建筑的时间更新，并设置更新 */
	public void use(String key) {
		if (mapSave.containsKey(key)) {
			BuildingSaveData data = mapSave.get(key);
			data.use(this.lastCheckTime);
		}
	}

	public static void registerAll() throws IOException {
		final String MODID = ESAPI.MODID;
		String[] mapJsonNames = IOHelper.getFilesFromResource(new ResourceLocation(MODID, "structures"));
		for (String path : mapJsonNames) {
			if (!path.endsWith(".nbt")) continue;
			NBTTagCompound nbt = IOHelper.readNBT(new ResourceLocation(MODID, "structures/" + path));
			if (path.lastIndexOf('.') != -1) path = path.substring(0, path.lastIndexOf('.'));
			Building building = new BuildingInherent(nbt, TextHelper.castToCamel(path));
			instance.addBuildingLib(path, building);
		}
		Buildings.init();
	}

	/** 加载一个建筑，从文件里 */
	private static BuildingSaveData loadBuildingFromTemp(String key) {
		File file;
		boolean isTemp = key.charAt(0) == '$';
		if (isTemp) file = ElementalSorcery.data.getFile("building/tmp/", key + ".nbt");
		else file = ElementalSorcery.data.getFile("building/json/", key + ".json");
		if (!file.exists()) return null;
		try {
			BuildingSaveData data = isTemp ? new BuildingSaveData(key, file) : new BuildingSaveDataJson(key, file);
			BuildingLib.instance.addBuilding(data);
			return data;
		} catch (IOException e) {
			file.delete();
		}
		return null;
	}

	/** 处理文件信息 */
	public void dealSave() {
		lastCheckTime = System.currentTimeMillis();
		LinkedList<String> removeList = new LinkedList<String>();
		for (Entry<String, BuildingSaveData> entry : mapSave.entrySet()) {
			BuildingSaveData data = entry.getValue();
			boolean out = data.trySave(lastCheckTime);
			if (out == false) removeList.add(entry.getKey());
		}
		for (String remove : removeList) this.mapSave.remove(remove);
	}

	public void releaseAllSaveData() {
		this.dealSave();
		this.mapSave.clear();
	}

}
