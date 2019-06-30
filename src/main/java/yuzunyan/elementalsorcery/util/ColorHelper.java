package yuzunyan.elementalsorcery.util;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.text.TextFormatting;

public class ColorHelper {
	static public int rgbToHSV(int R, int G, int B) {
		int max = Math.max(R, Math.max(G, B));
		int min = Math.min(R, Math.min(G, B));
		int V = Math.max(R, Math.max(G, B));
		int S = (max - min) * 255 / max;
		int H = 0;
		if (max - min != 0) {
			if (R == max)
				H = (G - B) / (max - min) * 60;
			else if (G == max)
				H = 120 + (B - R) / (max - min) * 60;
			else if (B == max)
				H = 240 + (R - G) / (max - min) * 60;
			if (H < 0)
				H = H + 360;
		}
		return (H << 16) | (S << 8) | (V << 0);
	}

	static public TextFormatting toTextFormatting(EnumDyeColor color) {
		switch (color) {
		case WHITE:
			return TextFormatting.WHITE;
		case ORANGE:
			return TextFormatting.GOLD;
		case MAGENTA:
			return TextFormatting.RED;
		case LIGHT_BLUE:
			return TextFormatting.BLUE;
		case YELLOW:
			return TextFormatting.YELLOW;
		case LIME:
			return TextFormatting.GREEN;
		case PINK:
			return TextFormatting.LIGHT_PURPLE;
		case GRAY:
			return TextFormatting.DARK_GRAY;
		case SILVER:
			return TextFormatting.GRAY;
		case CYAN:
			return TextFormatting.DARK_AQUA;
		case PURPLE:
			return TextFormatting.DARK_PURPLE;
		case BLUE:
			return TextFormatting.DARK_BLUE;
		case BROWN:
			return TextFormatting.GOLD;
		case GREEN:
			return TextFormatting.DARK_GREEN;
		case RED:
			return TextFormatting.DARK_RED;
		case BLACK:
			return TextFormatting.BLACK;
		}
		return TextFormatting.WHITE;
	}
}
