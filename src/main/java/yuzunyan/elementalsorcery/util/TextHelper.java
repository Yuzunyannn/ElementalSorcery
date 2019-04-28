package yuzunyan.elementalsorcery.util;

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

}
