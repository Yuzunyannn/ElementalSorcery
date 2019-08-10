package yuzunyannn.elementalsorcery.util;

import java.lang.reflect.Array;
import java.util.Random;

public class RandomHelper {

	static final public Random rand = new Random();

	// 随机选几个
	static public <T> T[] randomSelect(T... ints) {
		if (ints.length == 0)
			return null;
		ints = ints.clone();
		T[] lucky = (T[]) Array.newInstance(ints[0].getClass(), rand.nextInt(ints.length) + 1);
		int remain = ints.length;
		for (int i = 0; i < lucky.length; i++) {
			int rindex = rand.nextInt(remain--);
			lucky[i] = ints[rindex];
			ints[rindex] = ints[remain];
		}
		return lucky;
	}
}
