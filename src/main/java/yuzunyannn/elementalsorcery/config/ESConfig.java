package yuzunyannn.elementalsorcery.config;

import java.lang.reflect.Field;

import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.init.EntityRegistries;
import yuzunyannn.elementalsorcery.worldgen.WorldGeneratorES;

public class ESConfig {

	public final static ConfigGetter getter = new ConfigGetter();

	/** 初始化，并注入所有配置 */
	public static void init() {
		load(ESConfig.class);
		load(ElfPostOffice.class);
		load(WorldGeneratorES.class);
		load(EntityRegistries.class);
		load(BuildingLib.class);
		loadRegs(ESInit.ITEMS);
		loadRegs(ESInit.BLOCKS);
		loadRegs(ESInit.ELEMENTS);
		loadRegs(ESInit.MANTRAS);
	}

	public static void load(Object obj) {
		ConfigLoader.instance.load(obj, getter);
	}

	public static void close() {
		getter.close();
	}

	private static void loadRegs(Object esObject) {
		Class<?> cls = esObject.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			try {
				IForgeRegistryEntry reg = ((IForgeRegistryEntry<?>) field.get(esObject));
				ConfigLoader.instance.load(reg, getter);
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
