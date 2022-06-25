package yuzunyannn.elementalsorcery.util.helper;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

public class ColorHelper {

	static public Vec3d color(int color) {
		double r = ((color >> 16) & 0xff) / 255.0;
		double g = ((color >> 8) & 0xff) / 255.0;
		double b = ((color >> 0) & 0xff) / 255.0;
		return new Vec3d(r, g, b);
	}

	static public int color(Vec3d color) {
		return ((((int) (color.x * 255)) << 16) & 0xff0000) | ((((int) (color.y * 255)) << 8) & 0x00ff00)
				| ((((int) (color.z * 255)) << 0) & 0x0000ff);
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
