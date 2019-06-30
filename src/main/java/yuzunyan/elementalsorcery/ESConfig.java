package yuzunyan.elementalsorcery;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ESConfig {

	private static Configuration config;

	public final int BUILDING_MAX_REMAIN_DAYS;

	public ESConfig(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		ElementalSorcery.logger.info("开始加载config");
		config.load();

		Property property;
		property = config.get(Configuration.CATEGORY_GENERAL, "tmp_building_max_remain_days", 7,
				"[临时建筑保非活动停留最长][0表示不自动清除]");
		int BUILDING_MAX_REMAIN_DAYS = property.getInt();
		if (BUILDING_MAX_REMAIN_DAYS < 0) {
			property.set(7);
			BUILDING_MAX_REMAIN_DAYS = 7;
		}
		this.BUILDING_MAX_REMAIN_DAYS = BUILDING_MAX_REMAIN_DAYS;

		config.save();
		ElementalSorcery.logger.info("config加载完成");
	}

}
