package yuzunyannn.elementalsorcery.util.helper;

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

	public Color weight(Color other, float r) {
		this.r = this.r * (1 - r) + other.r * r;
		this.g = this.g * (1 - r) + other.g * r;
		this.b = this.b * (1 - r) + other.b * r;
		return this;
	}
}
