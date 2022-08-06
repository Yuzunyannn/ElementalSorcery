package yuzunyannn.elementalsorcery.api.element;

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

	public static final double ln2_4 = Math.log(2.4);
	public static final double ln2 = Math.log(2);

	/**
	 * @return power
	 */
	static public double fromFragmentUnit(Element element, double fragment) {
		return Math.pow(Math.E, Math.log(fragment) * ln2 / ln2_4);
	}

	/**
	 * @return fragment unit
	 */
	static public double toFragmentUnit(Element element, double power) {
		return Math.pow(2.4, Math.log(power) / ln2);
	}

	static public double toFragment(ElementStack estack) {
		return toFragmentUnit(estack.getElement(), estack.getPower()) * estack.getCount();
	}

	/**
	 * @return fragment
	 */
	static public double toFragment(Element element, double count, double power) {
		return toFragmentUnit(element, power) * count;
	}

	/**
	 * @return power
	 */
	static public double fromFragmentByCount(Element element, double fragment, double count) {
		return fromFragmentUnit(element, fragment / count);
	}

	/**
	 * @return count
	 */
	static public double fromFragmentByPower(Element element, double fragment, double targetPower) {
		return fragment / toFragmentUnit(element, targetPower);
	}

	/** 转化到元素片元，包括transition */
	static public double toMagicFragment(ElementStack estack) {
		double fragment = toFragment(estack);
		ElementTransition et = estack.getElement().getTransition();
		if (et == null) return fragment;
		return transitionFrom(estack.getElement(), fragment, et.getLevel());
	}

	static public double fromMagicFragmentByPower(Element element, double fragment, double power) {
		ElementTransition et = element.getTransition();
		if (et == null) return fromFragmentByPower(element, fragment, power);
		fragment = transitionTo(element, fragment, et.getLevel());
		return fromFragmentByPower(element, fragment, power);
	}

	static public double transitionTo(Element element, double fragment, double level) {
		return fragment / Math.pow(level, 1.125);
	}

	static public double transitionFrom(Element element, double fragment, double level) {
		return fragment * Math.pow(level, 1.125);
	}

}
