package yuzunyannn.elementalsorcery.util.math;

import net.minecraft.util.math.Vec3d;

public class Plane3d {

	/*
	 * ax + by + cz + d = 0
	 * */
	public final double a, b, c, d;

	public Plane3d(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public Plane3d() {
		this.a = this.b = this.c = this.d = 0;
	}

	static public Plane3d ofPointNormal(Vec3d point, Vec3d normal) {
		return new Plane3d(normal.x, normal.y, normal.z,
				-(point.x * normal.x + point.y * normal.y + point.z * normal.z));
	}

	static public Plane3d ofTwoVec(Vec3d vec0, Vec3d vec1) {
		return new Plane3d(vec0.y * vec1.z - vec0.z * vec1.y, vec0.z * vec1.x - vec0.x * vec1.z,
				vec0.x * vec1.y - vec0.y * vec1.x, 0);
	}

	public Vec3d getNormal() {
		return new Vec3d(this.a, this.b, this.c);
	}

	public Vec3d getProjection(Vec3d vec) {
		Vec3d n = getNormal();
		return vec.subtract(n.scale(vec.dotProduct(n)));
	}

}
