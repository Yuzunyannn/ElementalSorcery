package yuzunyannn.elementalsorcery.nodegui;

public class GActionEaseInOutBack extends GActionEase {

	public GActionEaseInOutBack(GActionTime action) {
		super(action);
	}

	@Override
	public double easeFunc(double x) {
		double c1 = 1.70158;
		double c2 = c1 * 1.525;
		return x < 0.5 ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
				: (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
	}

	public static double ease(double x) {
		double c1 = 1.70158;
		double c2 = c1 * 1.525;
		return x < 0.5 ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
				: (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
	}
}
