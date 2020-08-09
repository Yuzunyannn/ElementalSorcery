package yuzunyannn.elementalsorcery.util;

import java.lang.reflect.Array;
import java.util.Random;

public class RandomHelper {

	static final public Random rand = new Random();

	// 随机选几个
	static public <T> T[] randomSelect(T... ints) {
		return randomSelect(rand.nextInt(ints.length) + 1, ints);
	}

	// 随机选几个
	static public <T> T[] randomSelect(int count, T... ints) {
		if (ints.length == 0) return (T[]) Array.newInstance(Object.class, 0);
		ints = ints.clone();
		count = Math.min(count, ints.length);
		count = Math.max(0, count);
		T[] lucky = (T[]) Array.newInstance(ints[0].getClass(), count);
		if (count <= 0) return lucky;
		else if (count >= ints.length) return ints;
		int remain = ints.length;
		for (int i = 0; i < lucky.length; i++) {
			int rindex = rand.nextInt(remain--);
			lucky[i] = ints[rindex];
			ints[rindex] = ints[remain];
		}
		return lucky;
	}
}
