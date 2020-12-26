package yuzunyannn.elementalsorcery.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class ESConfig {

	private static Configuration config;

	/** 建筑的保存最大天数 */
	public final int BUILDING_MAX_REMAIN_DAYS;
	/** 是否展示默认元素的tooltip */
	public final boolean SHOW_ELEMENT_TOOLTIP;
	/** 传送门绘制等级 */
	public final int PORTAL_RENDER_TYPE;
	/** 每个玩家任务的最大上限 */
	public final int QUEST_LIMIT = 5;
	/** 包裹的最长保留时间 */
	public final float MAX_LIFE_TIME_OF_PARCEL;
	/** 生成相关配置 */
	public final ESConfigGenAndSpawn SPAWN;

	/** 手册的最大页面数 */
	public final int MANUAL_MAX_PAGES;

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
		// 元素手册的最大页数
		property = config.get(Configuration.CATEGORY_GENERAL, "enable_item_element_tooltip_show", false,
				"[鼠标移动到物品上，显示其默认具有的元素][仅在创造模式生效]");
		this.SHOW_ELEMENT_TOOLTIP = property.getBoolean();
		// 是否开启默认物品元素显示
		property = config.get(Configuration.CATEGORY_GENERAL, "manual_max_pages", 20, "[元素手册的最多页数]");
		this.MANUAL_MAX_PAGES = Math.max(property.getInt(), 2);
		// 传送门绘制
		property = config.get(Configuration.CATEGORY_GENERAL, "portal_render_type", 1,
				"[传送门绘制是使用的渲染类型][2为世界效果][1为粒子效果][0就一张图]");
		this.PORTAL_RENDER_TYPE = property.getInt();
		// 包裹的最长保持时间
		property = config.get(Configuration.CATEGORY_GENERAL, "max_life_time_of_parcel", 24 * 16,
				"[邮局储存包裹的最长保持时间，超过时间就会被清除，单位小时，如果为-1表示用不清除]");
		this.MAX_LIFE_TIME_OF_PARCEL = (float) property.getDouble();
		// 生成
		SPAWN = new ESConfigGenAndSpawn(config);

		config.save();
		ElementalSorcery.logger.info("config加载完成");
	}

}
