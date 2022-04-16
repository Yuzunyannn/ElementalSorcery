package yuzunyannn.elementalsorcery.util.element;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.ElementTransition;

public class ElementTransitionReactor {

	private static Element[] involvedElements;

	public static Element[] getInvolvedElements() {
		if (involvedElements != null) return involvedElements;
		List<Element> list = new ArrayList<>();
		for (Element element : Element.REGISTRY) if (element.getTransition() != null) list.add(element);
		return involvedElements = list.toArray(new Element[list.size()]);
	}

	// 上下文
	public float lastDetaAngle;
	public float lastDetaStep;
	public float lastDiffAngle;
	public float lastDiffStep;
	public Element lastTransitionSuggest;

	// 变量
	protected Element rElement = ElementStack.EMPTY.getElement();
	protected double fragment = 0;
	protected float angle, step = 0;

	public void reset() {
		rElement = ElementStack.EMPTY.getElement();
		fragment = 0;
		step = 0;
	}

	@Nonnull
	public Element getElement() {
		return rElement;
	}

	public void setElement(Element rElement) {
		this.rElement = rElement == null ? ElementStack.EMPTY.getElement() : rElement;
	}

	public double getFragment() {
		return fragment;
	}

	public float getAngle() {
		return angle;
	}

	public float getStep() {
		return step;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		ResourceLocation id = Element.getNameFromElement(rElement);
		if (id != null) nbt.setString("Eid", id.toString());
		nbt.setDouble("fragment", fragment);
		nbt.setFloat("angle", angle);
		nbt.setFloat("step", step);
		return nbt;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		ResourceLocation name = new ResourceLocation(nbt.getString("Eid"));
		rElement = Element.getElementFromName(name);
		if (rElement == null) rElement = ElementStack.EMPTY.getElement();
		fragment = nbt.getDouble("fragment");
		step = nbt.getFloat("step");
		angle = nbt.getFloat("angle");
	}

	public void transitTo(Element element) {
		this.rElement = element;
		ElementTransition et = element.getTransition();
		if (et == null) return;
		step = et.getLevel();
	}

	public void transit(Element element, double fragment) {
		ElementTransition et = element.getTransition();
		if (et == null) return;
		float ab = ElementTransition.formatAngle(et.getKernelAngle() - angle);
		float ba = 360 - ab;
		float dAngle = ab < ba ? (ab) : (-ba);
		float dStep = et.getLevel() - step;
		float cRate = (float) Math.max(fragment / (fragment + this.fragment), 0.05);
		lastDetaAngle = dAngle = dAngle * 0.1f * cRate;
		lastDetaStep = dStep = dStep * 0.1f * cRate;

		angle = ElementTransition.formatAngle(angle + dAngle);
		step = Math.max(step + dStep, 1);

		ElementTransition met = this.rElement.getTransition();
		if (met == null) return;
		this.lastDiffAngle = ElementTransition.formatAngle(met.getKernelAngle() - angle);
		this.lastDiffStep = Math.abs(met.getLevel() - step);

		this.lastTransitionSuggest = null;
		Element[] elements = getInvolvedElements();
		float maxDIFF = -1;
		for (Element targetElement : elements) {
			ElementTransition tet = targetElement.getTransition();
			float da = Math.min(ElementTransition.formatAngle(angle - tet.getKernelAngle()),
					ElementTransition.formatAngle(tet.getKernelAngle() - angle));
			if (da > tet.getRegionAngle() / 2) continue;
			float ds = Math.abs(tet.getLevel() - step);
			if (ds <= this.lastDiffStep / 8 || ds <= 0.05f) {
				float diff = Math.abs(this.lastDiffStep - ds);
				if (diff > maxDIFF) {
					maxDIFF = diff;
					this.lastTransitionSuggest = targetElement;
				}
			}
		}
	}

	public void insert(Element element, double fragment) {
		if (this.rElement == ElementStack.EMPTY.getElement()) {
			this.fragment += fragment;
			transitTo(element);
			return;
		}
		transit(element, fragment);
		this.fragment += fragment;
	}

}
