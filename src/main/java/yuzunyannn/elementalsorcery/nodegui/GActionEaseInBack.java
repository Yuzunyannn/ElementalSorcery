package yuzunyannn.elementalsorcery.nodegui;

public class GActionEaseInBack extends GActionEase {

	public GActionEaseInBack(GActionTime action) {
		super(action);
	}

	@Override
	public double easeFunc(double x) {
		double c1 = 1.70158;
		double c3 = c1 + 1;
		return c3 * x * x * x - c1 * x * x;
	}

	public static double ease(double x) {
		double c1 = 1.70158;
		double c3 = c1 + 1;
		return c3 * x * x * x - c1 * x * x;
	}
}
