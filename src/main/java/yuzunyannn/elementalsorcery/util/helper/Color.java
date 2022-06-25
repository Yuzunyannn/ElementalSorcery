package yuzunyannn.elementalsorcery.util.helper;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Color {

	public float r, g, b;

	public Color() {

	}

	public Color(float r, float g, float b) {
		setColor(r, g, b);
	}

	public Color(double r, double g, double b) {
		setColor(r, g, b);
	}

	public Color(int c) {
		setColor(c);
	}

	public Color(Vec3d c) {
		setColor(c);
	}

	public Color(Color c) {
		setColor(c);
	}

	public Color setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}

	public Color setColor(double r, double g, double b) {
		this.r = (float) r;
		this.g = (float) g;
		this.b = (float) b;
		return this;
	}

	public Color setColor(int c) {
		this.r = ((c >> 16) & 0xff) / 255.0f;
		this.g = ((c >> 8) & 0xff) / 255.0f;
		this.b = ((c >> 0) & 0xff) / 255.0f;
		return this;
	}

	public Color setColor(Vec3d c) {
		this.r = (float) c.x;
		this.g = (float) c.y;
		this.b = (float) c.z;
		return this;
	}

	public Color setColor(Color c) {
		this.r = c.r;
		this.g = c.g;
		this.b = c.b;
		return this;
	}

	public Vec3d toVec3d() {
		return new Vec3d(r, g, b);
	}

	public int toInt() {
		return ((((int) (r * 255)) << 16) & 0xff0000) | ((((int) (g * 255)) << 8) & 0x00ff00)
				| ((((int) (b * 255)) << 0) & 0x0000ff);
	}

	public Color copy() {
		return new Color(this);
	}

	public Color weight(Color other, float r) {
		this.r = this.r * (1 - r) + other.r * r;
		this.g = this.g * (1 - r) + other.g * r;
		this.b = this.b * (1 - r) + other.b * r;
		return this;
	}

	public Color add(float c) {
		this.r = MathHelper.clamp(this.r + c, 0, 1);
		this.g = MathHelper.clamp(this.g + c, 0, 1);
		this.b = MathHelper.clamp(this.b + c, 0, 1);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof Color) {
			Color other = (Color) obj;
			return (int) (other.r * 255) == (int) (this.r * 255) && (int) (other.g * 255) == (int) (this.g * 255)
					&& (int) (other.b * 255) == (int) (this.b * 255);
		}
		return false;
	}

	public Vec3d toHSV() {
		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));
		float V = max;
		float S = (max - min) / max;
		float H = 0;
		if (r == max) H = (g - b) / (max - min);
		else if (g == max) H = 2 + (b - r) / (max - min);
		else if (b == max) H = 4 + (r - g) / (max - min);
		H *= 60;
		if (H < 0) H = H + 360;
		return new Vec3d(H, S, V);
	}

	static public Color fromHSV(double h, double s, double v) {
		Color color = new Color(v, v, v);
		if (s == 0) return color;
		h = h / 60.0;
		int i = (int) h;
		double f = h - i;
		double a = v * (1.0 - s);
		double b = v * (1.0 - s * f);
		double c = v * (1.0 - s * (1.0 - f));
		if (i == 0) color.setColor(v, c, a);
		else if (i == 1) color.setColor(b, v, a);
		else if (i == 2) color.setColor(a, v, c);
		else if (i == 3) color.setColor(a, b, v);
		else if (i == 4) color.setColor(c, a, v);
		else color.setColor(v, a, b);
		return color;
	}
}
