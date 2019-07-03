package yuzunyannn.elementalsorcery.util.obj;

public class Vertex {
	public Vertex() {
		this(0, 0, 0);
	}

	public Vertex(float x, float y) {
		this(x, y, 0);
	}

	public Vertex(Vertex vertex) {
		this(vertex.x, vertex.y, vertex.z);
	}

	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float x, y, z;

	public Vertex toColor(int color) {
		x = (color >> 16) & 0x000000ff;
		y = (color >> 8) & 0x000000ff;
		z = (color >> 0) & 0x000000ff;
		x /= 255;
		y /= 255;
		z /= 255;
		return this;
	}

	public int toColor() {
		return ((((int) (x * 255)) << 16) & 0xff0000) | ((((int) (y * 255)) << 8) & 0x00ff00)
				| ((((int) (z * 255)) << 0) & 0x0000ff);
	}
}
