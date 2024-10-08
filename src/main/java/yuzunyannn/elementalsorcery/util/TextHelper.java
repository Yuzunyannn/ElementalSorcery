package yuzunyannn.elementalsorcery.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.api.ESAPI;

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

	static final String[] ABBREVIATED_TABLE = new String[] { "k", "M", "G", "T", "P", "E", "Z", "Y" };

	static public String toAbbreviatedNumber(double n, int significant) {
		for (int i = 0; i < ABBREVIATED_TABLE.length; i++) {
			double c = n / 1000;
			if (c < 1) {
				if (i == 0) return String.format("%.0f", n);
				else return String.format("%." + significant + "f" + ABBREVIATED_TABLE[i - 1], n);
			}
			n = c;
		}
		return String.format("%." + significant + "f" + ABBREVIATED_TABLE[ABBREVIATED_TABLE.length - 1], n);
	}

	static public String toAbbreviatedNumber(double n) {
		return toAbbreviatedNumber(n, 3);
	}

	static public String castToCapital(String str) {
		if (str.isEmpty()) return "";
		String[] strs = str.split("_");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			str = strs[i];
			String t = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
			builder.append(t);
			if (i < strs.length - 1) builder.append(" ");
		}
		return builder.toString();
	}

	static public String[] splitCapital(String str) {
		List<String> strs = new ArrayList<>();
		int j = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch >= 0x41 && ch <= 0x5a) {
				strs.add(str.substring(j, i - 1));
				j = i;
			}
		}
		strs.add(str.substring(j));
		return strs.toArray(new String[strs.size()]);
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

	static public String toRoman(int num) {
		String[][] c = { { "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX" },
				{ "", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC" },
				{ "", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM" }, { "", "M", "MM", "MMM" } };
		StringBuffer ret = new StringBuffer();
		ret.append(c[3][num / 1000 % 4]);
		ret.append(c[2][num / 100 % 10]);
		ret.append(c[1][num / 10 % 10]);
		ret.append(c[0][num % 10]);
		return ret.toString();
	}

	static public ResourceLocation toResourceLocation(String id, String defaultDomain) {
		if (id.indexOf(':') == -1) return new ResourceLocation(defaultDomain, id);
		return new ResourceLocation(id);
	}

	static public ResourceLocation toESResourceLocation(String id) {
		return toResourceLocation(id, ESAPI.MODID);
	}

	static public ResourceLocation toMCResourceLocation(String id) {
		return toResourceLocation(id, "minecraft");
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

	public static String toTime(double tick) {
		double time = tick / 20.0;
		if (time < 60) return String.format("%.0fs", time);
		time = time / 60;
		if (time < 60) {
			double n = time - Math.floor(time);
			String min = String.format("%.0fm", time);
			if (n == 0) return min;
			return min + "-" + toTime(n * 60 * 20);
		}
		time = time / 60;
		if (time < 24) {
			double n = time - Math.floor(time);
			String hour = String.format("%.0fh", time);
			if (n == 0) return hour;
			return hour + "-" + toTime(n * 60 * 60 * 20);
		}
		time = time / 24;
		double n = time - Math.floor(time);
		String day = String.format("%.0fd", time);
		if (n == 0) return day;
		return day + "-" + toTime(n * 24 * 60 * 60 * 20);
	}

	public static String format$(String str, Function<String, String> sf) {
		Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(str);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String key = matcher.group(1);
			String replacement = sf.apply(key);
			matcher.appendReplacement(result, replacement);
		}
		matcher.appendTail(result);
		return result.toString();
	}
}
