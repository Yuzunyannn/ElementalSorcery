package yuzunyannn.elementalsorcery.config;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.init.EntityRegistries;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.worldgen.WorldGeneratorES;

public class ESConfig {

	public final static ConfigGetter getter = new ConfigGetter();

	private static boolean isSync = false;

	/** 基础初始化，在一切初始化的最前面，真个游戏应该只调用一次 */
	public static void initBase() {
		ConfigTranslate.getOrLoadJsonMap();
	}

	/** 核心初始化初始化，并注入所有配置 */
	public static void init() {
		isSync = false;
		loadAll(getter);
		getter.close();
		ConfigTranslate.foreverColse();
	}

	public static void restore() {
		sync(getter.getSyncData());
	}

	public static void sync(NBTTagCompound configNBT) {
		isSync = true;
		loadAll(new ConfigNBTGetter(configNBT));
	}

	private static void loadAll(IConfigGetter getter) {
		load(ESConfig.class, getter);
		load(ElfPostOffice.class, getter);
		load(ElfChamberOfCommerce.class, getter);
		load(ElfConfig.class, getter);
		load(WorldGeneratorES.class, getter);
		load(EntityRegistries.class, getter);
		load(BuildingLib.class, getter);
		load(TileMDBase.class, getter);
		load(TileEntityNetwork.class, getter);
		load(ElementInventory.class, getter);
		load(Researcher.class, getter);
		load(ElfProfession.class, getter);
		loadList(ESInit.ES_TILE_ENTITY, getter);
		loadRegs(ESObjects.ITEMS, getter);
		loadRegs(ESObjects.BLOCKS, getter);
		loadRegs(ESObjects.ELEMENTS, getter);
		loadRegs(ESObjects.MANTRAS, getter);
	}

	private static void load(Object obj, IConfigGetter getter) {
		ConfigLoader.instance.load(obj, getter, isSync);
	}

	private static void loadList(List<?> list, IConfigGetter getter) {
		for (Object obj : list) load(obj, getter);
	}

	private static void loadRegs(Object esObject, IConfigGetter getter) {
		Class<?> cls = esObject.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			try {
				IForgeRegistryEntry reg = ((IForgeRegistryEntry<?>) field.get(esObject));
				ConfigLoader.instance.load(reg, getter, false);
			} catch (Exception e) {
				ESAPI.logger.warn("注入注册对象配置时出现异常", e);
			}
		}
	}

	@Config
	public static int QUEST_LIMIT = 5;

	@Config
	public static int PORTAL_RENDER_TYPE = 1;

	@Config
	public static boolean ENABLE_ITEM_ELEMENT_TOOLTIP_SHOW = false;

}
