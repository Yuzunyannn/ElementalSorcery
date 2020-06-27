package yuzunyannn.elementalsorcery.util;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class TextHelper {

	static public void addInfo(List<String> tooltip, String str, Object... parameters) {
		tooltip.add(I18n.format(str, parameters));
	}

	static public void addInfo(List<String> tooltip, String str, TextFormatting formatting, Object... parameters) {
		tooltip.add(formatting + I18n.format(str, parameters));
	}

	static public void addInfoCheckLine(List<String> tooltip, String str, Object... parameters) {
		String[] strs = I18n.format(str, parameters).split("\n");
		for (String tmp : strs) {
			tooltip.add(tmp);
		}
	}

	static public String castToCamel(String str) {
		String[] strs = str.split("_");
		if (strs.length == 1) return strs[0].toLowerCase();
		StringBuilder builder = new StringBuilder();
		builder.append(strs[0].toLowerCase());
		for (int i = 1; i < strs.length; i++) {
			str = strs[i];
			String t = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
			builder.append(t);
		}
		return builder.toString();
	}
}
