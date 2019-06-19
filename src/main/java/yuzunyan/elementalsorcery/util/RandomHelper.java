package yuzunyan.elementalsorcery.util;

import java.util.Random;

public class RandomHelper {

	static final public Random rand = new Random();

	// 随机选几个
	static public int[] randomSelect(int... ints) {
		if (ints.length == 0)
			return null;
		int n = RandomHelper.rand.nextInt(ints.length) + 1;
		int[] lucky = new int[n];
		final double rate = n / ints.length;
		n = Math.min(n, ints.length - n);
		int at = 0;
		for (int i = 0; i < n; i++) {
			if (Math.random() < rate) {
				lucky[at++] = ints[i];
			}
		}
		int rest = lucky.length - at;
		for (int i = 0; i < rest; i++) {
			lucky[at++] = ints[i + n];
		}
		return lucky;
	}
}
