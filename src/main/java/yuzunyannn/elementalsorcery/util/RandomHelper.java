package yuzunyannn.elementalsorcery.util;

import java.util.Random;

public class RandomHelper {

	static final public Random rand = new Random();

	// 随机选几个
	static public int[] randomSelect(int... ints) {
		if (ints.length == 0)
			return null;
		ints = ints.clone();
		int[] lucky = new int[rand.nextInt(ints.length) + 1];
		int remain = ints.length;
		for (int i = 0; i < lucky.length; i++) {
			int rindex = rand.nextInt(remain--);
			lucky[i] = ints[rindex];
			ints[rindex] = ints[remain];
		}
		return lucky;
	}
}
