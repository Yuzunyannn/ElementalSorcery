package yuzunyannn.elementalsorcery.util.element;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class ElementStackDouble implements INBTSerializable<NBTTagCompound> {

	private double stackSize;
	private Element element;
	private double power;

	public ElementStackDouble() {
		this((Element) null);
	}

	public ElementStackDouble(Element element) {
		this(element, 1);
	}

	public ElementStackDouble(Element element, double size) {
		this(element, size, 0);
	}

	public ElementStackDouble(Element element, double size, double power) {
		setElement(element);
		this.stackSize = size >= 0 ? size : 0;
		this.power = power >= 0 ? power : 0;
	}

	public ElementStackDouble(NBTTagCompound compound) {
		this.deserializeNBT(compound);
	}

	public void setElement(Element element) {
		this.element = element == null ? ElementStack.EMPTY.getElement() : element;
	}

	public Element getElement() {
		return element;
	}

	public double getPower() {
		return power;
	}

	public double getCount() {
		if (this.isEmpty()) return 0;
		return stackSize;
	}

	public void setCount(double size) {
		stackSize = size;
		if (stackSize < 0) stackSize = 0;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public boolean isEmpty() {
		if (this.element == ElementStack.EMPTY.getElement()) return true;
		else if (element != null) {
			if (stackSize <= 0) return true;
			return false;
		} else return true;
	}

	public ElementStackDouble copy() {
		ElementStackDouble stack = new ElementStackDouble();
		stack.become(this);
		return stack;
	};

	public void become(ElementStack other) {
		setElement(other.getElement());
		setCount(other.getCount());
		setPower(other.getPower());
	}

	public void become(ElementStackDouble other) {
		setElement(other.getElement());
		setCount(other.getCount());
		setPower(other.getPower());
	}

	public int getColor() {
		return this.element.getColor(this.asElementStack());
	}

	public boolean mergeElement(ElementStack src) {
		return mergeElement(src, false);
	}

	public boolean mergeElement(ElementStack src, boolean simulate) {
		if (src.isEmpty()) return false;
		if (isEmpty()) {
			if (simulate) return true;
			become(src);
			return true;
		}
		if (src.getElement() != element) return false;
		if (simulate) return true;

		double count = this.getCount() + src.getCount();

		double weightDst = this.getCount();
		double weightSrc = src.getCount();

		if (this.power > src.getPower()) weightDst = weightDst + 10;
		else weightSrc = weightSrc + 10;

		double power = (this.power * weightDst + src.getPower() * weightSrc) / (weightSrc + weightDst);

		setCount(count);
		setPower(power);

		return true;
	}

	public void grow(double size) {
		setCount(getCount() + size);
	}

	public void shrink(double size) {
		this.grow(-size);
	}

	public void disgrow(ElementStack estack) {
		if (estack.isEmpty()) return;
		if (!this.areSameType(estack)) return;
		this.grow(-estack.getCount());
		if (this.isEmpty()) return;
		double total = estack.getCount() + this.getCount();
		this.setPower((this.getPower() * total - estack.getPower() * estack.getCount()) / this.getCount());
	}

	public void grow(ElementStack estack) {
		if (estack.isEmpty()) return;
		if (!this.areSameType(estack)) return;
		double total = estack.getCount() + this.getCount();
		total = total == 0 ? 1 : total;
		this.setPower((estack.getPower() * estack.getCount() + this.getPower() * this.getCount()) / total);
		this.grow(estack.getCount());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		ResourceLocation name = new ResourceLocation(nbt.getString("id"));
		this.setElement(Element.getElementFromName(name));
		if (element == null) this.setElement(ElementStack.EMPTY.getElement());
		this.setCount(nbt.getDouble("size"));
		this.setPower(nbt.getDouble("power"));
	}

	public void writeToNBT(NBTTagCompound nbt) {
		ResourceLocation resourcelocation = Element.getNameFromElement(this.element);
		nbt.setString("id", resourcelocation == null ? "" : resourcelocation.toString());
		nbt.setDouble("size", stackSize);
		nbt.setDouble("power", power);
	}

	public boolean areSameType(ElementStack estack) {
		if (getElement() != estack.getElement()) return false;
		return true;
	}

	public ElementStack toElementStack() {
		return new ElementStack(element, (int) stackSize, (int) power);
	}

	public ElementStack asElementStack() {
		return new ElementStackVest();
	}

	public class ElementStackVest extends ElementStack {
		@Override
		protected void setElement(Element element) {
			ElementStackDouble.this.setElement(element);
		}

		@Override
		public Element getElement() {
			return ElementStackDouble.this.getElement();
		}

		@Override
		public int getCount() {
			return (int) ElementStackDouble.this.getCount();
		}

		@Override
		public void setCount(int size) {
			ElementStackDouble.this.setCount(size);
		}

		@Override
		public void setPower(int power) {
			ElementStackDouble.this.setPower(power);
		}

		@Override
		public int getPower() {
			return (int) ElementStackDouble.this.getPower();
		}

		@Override
		public boolean isEmpty() {
			return ElementStackDouble.this.isEmpty();
		}

		@Override
		public void grow(int count) {
			ElementStackDouble.this.grow(count);
		}

		@Override
		public void grow(ElementStack estack) {
			ElementStackDouble.this.grow(estack);
		}

		@Override
		public void disgrow(ElementStack estack) {
			ElementStackDouble.this.disgrow(estack);
		}
	}

}
