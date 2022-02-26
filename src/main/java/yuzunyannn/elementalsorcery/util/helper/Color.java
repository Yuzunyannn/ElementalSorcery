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

	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void setColor(double r, double g, double b) {
		this.r = (float) r;
		this.g = (float) g;
		this.b = (float) b;
	}

	public void setColor(int c) {
		this.r = ((c >> 16) & 0xff) / 255.0f;
		this.g = ((c >> 8) & 0xff) / 255.0f;
		this.b = ((c >> 0) & 0xff) / 255.0f;
	}

	public void setColor(Vec3d c) {
		this.r = (float) c.x;
		this.g = (float) c.y;
		this.b = (float) c.z;
	}

	public Vec3d toVec3d() {
		return new Vec3d(r, g, b);
	}
}
