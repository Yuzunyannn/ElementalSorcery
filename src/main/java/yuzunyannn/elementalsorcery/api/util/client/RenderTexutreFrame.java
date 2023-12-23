package yuzunyannn.elementalsorcery.api.util.client;

public class RenderTexutreFrame {

	public static final RenderTexutreFrame FULL_FRAME = new RenderTexutreFrame(0, 0, 16, 16, 16, 16);

	final public float width;
	final public float height;
	final public float x, y;
	final public float texWidth;
	final public float texHeight;

	public RenderTexutreFrame(float x, float y, float width, float height, float texWidth, float texHeight) {
		this.x = x / texWidth;
		this.y = y / texHeight;
		this.width = width / texWidth;
		this.height = height / texHeight;
		this.texWidth = texWidth;
		this.texHeight = texHeight;
	}

}
