package yuzunyannn.elementalsorcery.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.IOHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ConfigTranslate {

	private static boolean isClose = false;
	private static JsonObject langMap;

	public static boolean isClose() {
		return isClose;
	}

	public static JsonObject getOrLoadJsonMap() {
		if (isClose) return null;
		if (langMap != null) return langMap;
		load();
		return langMap;
	}

	private static String defaultLang = "en_us";

	private static void load() {
		try {
			try {
				LanguageManager mgr = Minecraft.getMinecraft().getLanguageManager();
				Language lang = mgr.getCurrentLanguage();
				defaultLang = lang.getLanguageCode();
			} catch (Throwable e) {}

			langMap = IOHelper.readJson(TextHelper.toESResourceLocation("config_lang/en_us.json"));
			if (!defaultLang.equals("en_us")) langMap
					.merge(IOHelper.readJson(TextHelper.toESResourceLocation("config_lang/" + defaultLang + ".json")));

		} catch (Throwable e) {}
	}

	public static void foreverColse() {
		isClose = true;
		if (langMap != null) langMap = null;
		if (keyStatisticsMap != null) {
			keyStatisticsMap.save(ElementalSorcery.data.getFile("develop/confg_lang", defaultLang + ".json"), true);
			keyStatisticsMap = null;
		}

	}

	private static JsonObject keyStatisticsMap;

	public static void keyStatistics(String key) {
		if (isClose) return;
		if (keyStatisticsMap == null) keyStatisticsMap = new JsonObject();
		JsonObject langMap = getOrLoadJsonMap();
		if (langMap.hasString(key)) keyStatisticsMap.set(key, langMap.getString(key));
		else keyStatisticsMap.set(key, "nothing!");
	}

}
