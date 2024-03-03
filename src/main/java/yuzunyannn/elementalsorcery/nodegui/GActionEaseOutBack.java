package yuzunyannn.elementalsorcery.nodegui;

public class GActionEaseOutBack extends GActionEase {

	public GActionEaseOutBack(GActionTime action) {
		super(action);
	}

	@Override
	public double easeFunc(double x) {
		double c1 = 1.70158;
		double c3 = c1 + 1;
		return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
	}

}
