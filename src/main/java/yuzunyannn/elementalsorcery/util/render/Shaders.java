package yuzunyannn.elementalsorcery.util.render;

import java.io.IOException;

import net.minecraft.client.Minecraft;

public class Shaders {

	public static Shader HSV;
	public static Shader GRAY;

	public static void init() throws IOException {
		Minecraft mc = Minecraft.getMinecraft();
		HSV = new Shader("shaders/hsv.fsh");
		GRAY = new Shader("shaders/gray.fsh");
	}

}
