package yuzunyan.elementalsorcery.util;

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
}
