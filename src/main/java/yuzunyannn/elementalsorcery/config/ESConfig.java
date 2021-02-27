package yuzunyannn.elementalsorcery.config;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.init.EntityRegistries;
import yuzunyannn.elementalsorcery.worldgen.WorldGeneratorES;

public class ESConfig {

	public final static ConfigGetter getter = new ConfigGetter();

	private static boolean isSync = false;

	/** 初始化，并注入所有配置 */
	public static void init() {
		isSync = false;
		loadAll(getter);
		getter.close();
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
		load(WorldGeneratorES.class, getter);
		load(EntityRegistries.class, getter);
		load(BuildingLib.class, getter);
		loadList(ESInit.ES_TILE_ENTITY, getter);
		loadRegs(ESInit.ITEMS, getter);
		loadRegs(ESInit.BLOCKS, getter);
		loadRegs(ESInit.ELEMENTS, getter);
		loadRegs(ESInit.MANTRAS, getter);
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
				ElementalSorcery.logger.warn("注入注册对象配置时出现异常", e);
			}
		}
	}

	@Config(note = "[每个玩家同时最多可以领取的任务个数]")
	public static int QUEST_LIMIT = 5;

	@Config(note = "[传送门绘制是使用的渲染类型][2为世界效果][1为粒子效果][0就一张图]")
	public static int PORTAL_RENDER_TYPE = 1;

	@Config(note = "[鼠标移动到物品上，显示其默认具有的元素][仅在创造模式生效]")
	public static boolean ENABLE_ITEM_ELEMENT_TOOLTIP_SHOW = false;

}
