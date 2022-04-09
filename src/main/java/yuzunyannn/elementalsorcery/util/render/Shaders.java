package yuzunyannn.elementalsorcery.util.render;

import java.io.IOException;

import net.minecraft.client.Minecraft;

public class Shaders {

	public static Shader JCOLOR;
	public static Shader HSV;
	public static Shader GRAY;
	public static Shader BlockDisintegrate;
	public static Shader BlockIceRockCrystal;
	public static Shader BlockMeltCauldron;
	public static Shader RGBColorMapping;

	public static void init() throws IOException {
		Minecraft mc = Minecraft.getMinecraft();
		JCOLOR = new Shader("shaders/just_color.fsh");
		HSV = new Shader("shaders/hsv.fsh");
		GRAY = new Shader("shaders/gray.fsh");
		BlockDisintegrate = new Shader("shaders/block_disintegrate.fsh");
		BlockIceRockCrystal = new Shader("shaders/block_ice_rock_crystal.fsh");
		BlockMeltCauldron = new Shader("shaders/block_melt_cauldron.fsh");
		RGBColorMapping = new Shader("shaders/rgb_color_mapping.fsh");
	}

}
