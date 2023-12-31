package yuzunyannn.elementalsorcery.api.util.client;

public class RenderRect {

	public final float top;
	public final float bottom;
	public final float left;
	public final float right;

	public RenderRect(float top, float bottom, float left, float right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}
	
	public RenderRect(double top, double bottom, double left, double right) {
		this.top = (float) top;
		this.bottom = (float) bottom;
		this.left = (float) left;
		this.right = (float) right;
	}

	public RenderRect move(float x, float y) {
		return new RenderRect(top + y, bottom + y, left + x, right + x);
	}
	
	public RenderRect move(double x, double y) {
		return new RenderRect(top + y, bottom + y, left + x, right + x);
	}

}
