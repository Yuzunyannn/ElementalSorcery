package yuzunyannn.elementalsorcery.util.text;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.ElementalSorcery;

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

	public static String castToUnderline(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) if (i > 0) sb.append('_');
			sb.append(Character.toLowerCase(c));
		}
		return sb.toString();
	}

	static public ResourceLocation toResourceLocation(String id, String defaultDomain) {
		if (id.indexOf(':') == -1) return new ResourceLocation(defaultDomain, id);
		return new ResourceLocation(id);
	}

	static public ResourceLocation toESResourceLocation(String id) {
		return toResourceLocation(id, ElementalSorcery.MODID);
	}

	public static String replaceStringWith$(String str, Function<Integer, String> getter) {
		Pattern p = Pattern.compile("\\$(\\d+)");
		String[] strs = p.split(str);
		Matcher m = p.matcher(str);
		int i = 0;
		StringBuilder builder = new StringBuilder();
		while (m.find()) {
			builder.append(strs[i]);
			builder.append(getter.apply(Integer.parseInt(m.group(1))));
			i++;
		}
		if (i < strs.length) builder.append(strs[i]);
		return builder.toString();
	}
}
