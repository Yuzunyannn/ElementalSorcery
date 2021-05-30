package yuzunyannn.elementalsorcery.util.render;

import java.io.IOException;

public class Shaders {

	public static Shader HSV;

	public static void init() throws IOException {
		HSV = new Shader("shaders/hsv.fsh");
	}

}
