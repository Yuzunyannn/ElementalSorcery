package yuzunyannn.elementalsorcery.util;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomHelper {

	static final public Random rand = new Random();

	static public int randomRange(int min, int max) {
		return (int) (min + (max - min) * rand.nextFloat());
	}

	static public float randomRange(float min, float max) {
		return min + (max - min) * rand.nextFloat();
	}

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

	static public class WeightRandom<T> {

		private class Pair {
			final T obj;
			double weight;

			protected Pair(T obj, double weight) {
				this.obj = obj;
				this.weight = weight;
			}
		}

		protected List<Pair> list = new LinkedList<>();

		public void add(T obj, double weight) {
			list.add(new Pair(obj, weight));
		}

		public void fixWeight(double weight) {
			for (Pair pair : list) pair.weight += weight;
		}

		public T get(Random rand) {
			if (this.isEmpty()) return null;
			double we = 0;
			for (Pair pair : list) we += pair.weight;
			double at = rand.nextFloat() * we;
			we = 0;
			for (Pair pair : list) {
				we += pair.weight;
				if (at < we) return pair.obj;
			}
			return list.get(rand.nextInt(list.size())).obj;
		}

		public T get() {
			return this.get(rand);
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}
	}
}
