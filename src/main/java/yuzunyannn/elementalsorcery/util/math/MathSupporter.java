package yuzunyannn.elementalsorcery.util.math;

public class MathSupporter {

	static public double easeInOutElastic(double x) {
		final double c5 = (2 * Math.PI) / 4.5;
		return x == 0 ? 0
				: x == 1 ? 1
						: x < 0.5 ? -(Math.pow(2, 20 * x - 10) * Math.sin((20 * x - 11.125) * c5)) / 2
								: (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1;
	}

	static public double easeOutBack(double x) {
		double rx = x - 1;
		double dx = rx * rx;
		return 1 + 2.70158 * dx * rx + 1.70158 * dx;
	}
}
