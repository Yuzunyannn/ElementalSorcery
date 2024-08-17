package yuzunyannn.elementalsorcery.util.math;

import java.util.List;
import java.util.function.Function;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

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

	static public <T> int binarySearch(List<T> list, Function<T, Double> checker) {
		if (list.isEmpty()) return -1;
		int i = 0;
		int j = list.size() - 1;
		while (true) {
			int n = (i + j) / 2;
			T obj = list.get(n);
			double ret = checker.apply(obj);
			if (ret == 0) return n;

			if (ret > 0) {
				i = n + 1;
				if (i > j) return -i - 1;
			} else {
				j = n - 1;
				if (i > j) return -i - 1;
			}
		}
	}

	// 方差
	public static float variance(float[] nums) {
		float average = 0;
		for (float n : nums) average += n;
		average = average / nums.length;

		float variance = 0;
		for (float n : nums) variance += (n - average) * (n - average);
		variance = variance / nums.length;

		return variance;
	}

	public static int digit(float num) {
		int n = 0;
		while (num >= 1) {
			num /= 10;
			n++;
		}
		return n;
	}

	public static Vec3d rotation(Vec3d point, Vec3d axis, double rotation) {
		double u = point.x;
		double v = point.y;
		double w = point.z;

		double x = axis.x;
		double y = axis.y;
		double z = axis.z;

		float t = (float) rotation;

		double nx = u * MathHelper.cos(t) + (y * w - z * v) * MathHelper.sin(t) + x * (x * u + y * v + z * w) * (1 - MathHelper.cos(t));
		double ny = v * MathHelper.cos(t) + (z * u - x * w) * MathHelper.sin(t) + y * (x * u + y * v + z * w) * (1 - MathHelper.cos(t));
		double nz = w * MathHelper.cos(t) + (x * v - y * u) * MathHelper.sin(t) + z * (x * u + y * v + z * w) * (1 - MathHelper.cos(t));

		return new Vec3d(nx, ny, nz);
	}

	public static double decimalForIntegralization(double num) {
		return -(num - Math.floor(num));
	}

	public static boolean contains(AxisAlignedBB box, Vec3i vec) {
		if (vec.getX() > box.minX && vec.getX() < box.maxX) {
			if (vec.getY() > box.minY && vec.getY() < box.maxY) {
				return vec.getZ() > box.minZ && vec.getZ() < box.maxZ;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
