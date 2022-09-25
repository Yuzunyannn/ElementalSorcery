package yuzunyannn.elementalsorcery.util.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Line3d {

	/*
	 * (x-a)/m = (y-b)/n = (z-c)/p
	 */
	public final double a, b, c;
	public final double m, n, p;

	public Line3d() {
		this.a = this.b = this.c = 0;
		this.m = this.n = this.p = 1;
	}

	public Line3d(double a, double b, double c, double m, double n, double p) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.m = m;
		this.n = n;
		this.p = p;
	}

	static public Line3d ofPointDirection(Vec3d point, Vec3d direction) {
		return new Line3d(point.x, point.y, point.z, direction.x, direction.y, direction.z);
	}

	public Vec3d getDirection() {
		return new Vec3d(this.m, this.n, this.p);
	}

	public double includedAngle(Line3d other) {
		double _l = MathHelper.sqrt(this.m * this.m + this.n * this.n + this.p * this.p)
				* MathHelper.sqrt(other.m * other.m + other.n * other.n + other.p * other.p);
		return (this.m * other.m + this.n * other.n + this.p * other.p) / _l;
	}

	public double lengthOfPoionToLine(Vec3d point) {
		Vec3d p1 = new Vec3d(1, (1 - a) * n / m + b, (1 - a) * p / m + c);
		Vec3d p2 = new Vec3d(2, (2 - a) * n / m + b, (2 - a) * p / m + c);
		Vec3d p01 = point.subtract(p1);
		Vec3d p02 = point.subtract(p2);
		return p01.crossProduct(p02).length() / p2.subtract(p1).length();
	}

}
