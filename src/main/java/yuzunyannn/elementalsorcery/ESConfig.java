package yuzunyannn.elementalsorcery;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ESConfig {

	private static Configuration config;

	/**建筑的保存最大天数*/
	public final int BUILDING_MAX_REMAIN_DAYS;
	/**手册的最大页面数*/
	public final int MANUAL_MAX_PAGES = 20;
	/**是否展示默认元素的tooltip*/
	public final boolean SHOW_ELEMENT_TOOLTIP;

	public ESConfig(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		ElementalSorcery.logger.info("开始加载config");
		config.load();

		Property property;
		// 建筑最多保存天数
		property = config.get(Configuration.CATEGORY_GENERAL, "tmp_building_max_remain_days", 7,
				"[临时建筑保非活动停留最长][0表示不自动清除]");
		int BUILDING_MAX_REMAIN_DAYS = property.getInt();
		if (BUILDING_MAX_REMAIN_DAYS < 0) {
			property.set(7);
			BUILDING_MAX_REMAIN_DAYS = 7;
		}
		this.BUILDING_MAX_REMAIN_DAYS = BUILDING_MAX_REMAIN_DAYS;
		// 是否开启默认物品元素显示
		property = config.get(Configuration.CATEGORY_GENERAL, "enable_item_element_tooltip_show", false,
				"[鼠标移动到物品上，显示其默认具有的元素][仅在创造模式生效]");
		this.SHOW_ELEMENT_TOOLTIP = property.getBoolean();

		config.save();
		ElementalSorcery.logger.info("config加载完成");
	}

}
