package yuzunyannn.elementalsorcery.util.helper;

public class NumberHelper {

	public static float variance(float[] nums) {
		float average = 0;
		for (float n : nums) average += n;
		average = average / nums.length;

		float variance = 0;
		for (float n : nums) variance += (n - average) * (n - average);
		variance = variance / nums.length;

		return variance;
	}

}
