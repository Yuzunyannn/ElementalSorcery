package yuzunyannn.elementalsorcery.element;

import net.minecraft.util.math.MathHelper;

public class ElementTransition {

	protected float level;
	protected float kernelAngle;
	protected float regionAngle;

	public ElementTransition(float level, float kernelAngle, float regionAngle) {
		this.level = Math.max(level, 1);
		this.kernelAngle = formatAngle(kernelAngle);
		this.regionAngle = MathHelper.clamp(regionAngle, 0, 360);
	}

	public static float formatAngle(float angle) {
		while (angle > 360) angle -= 360;
		while (angle < 0) angle += 360;
		return angle;
	}

	/** 获取轨道等级，必须大于1 */
	public float getLevel() {
		return level;
	}

	/** 获取圈环核心角度[0,360]，顺时针，0为12点钟 */
	public float getKernelAngle() {
		return kernelAngle;
	}

	/** 以圈换核心展开的角度[0,360] */
	public float getRegionAngle() {
		return regionAngle;
	}

}
