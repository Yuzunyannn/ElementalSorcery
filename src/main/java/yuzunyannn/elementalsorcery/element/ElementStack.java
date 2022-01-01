package yuzunyannn.elementalsorcery.element;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class ElementStack implements INBTSerializable<NBTTagCompound> {

	public final static ElementStack EMPTY = new Unchangeable();

	/** 修改无效的物品栈 */
	public static class Unchangeable extends ElementStack {

		private Unchangeable() {
			super();
		}

		public Unchangeable(Element element) {
			super(element, 1);
		}

		public Unchangeable(Element element, int size) {
			super(element, size, 0);
		}

		public Unchangeable(Element element, int size, int power) {
			super(element, size, power);
		}

		@Override
		protected void setElement(Element element) {
		}

		@Override
		public void become(ElementStack estack) {
		}

		@Override
		public void setCount(int size) {
		}

		@Override
		public void setPower(int power) {
		}
	}

	static public ElementStack magic(int size, int power) {
		return new ElementStack(ESObjects.ELEMENTS.MAGIC, size, power);
	}

	/** 元素数量 */
	private int stackSize;
	/** 元素 */
	private Element element;
	/** 元素能量 */
	private int power;
	/** 自定义数据 */
	private NBTTagCompound stackTagCompound;

	protected ElementStack() {

	}

	public ElementStack(Element element) {
		this(element, 1);
	}

	public ElementStack(Element element, int size) {
		this(element, size, 0);
	}

	public ElementStack(Element element, int size, int power) {
		setElement(element);
		setCount(size >= 0 ? size : 0);
		setPower(power >= 0 ? power : 0);
	}

	public ElementStack(NBTTagCompound compound) {
		this.deserializeNBT(compound);
	}

	protected void setElement(Element element) {
		this.element = element == null ? EMPTY.element : element;
	}

	public void become(ElementStack estack) {
		this.setElement(estack.getElement());
		setCount(estack.getCount());
		setPower(estack.getPower());
	}

	public ElementStack copy() {
		ElementStack itemstack = new ElementStack();
		itemstack.become(this);
		return itemstack;
	}

	/** 获取元素 */
	public Element getElement() {
		return element;
	}

	/** 获取元素颜色 */
	public int getColor() {
		return getElement().getColor(this);
	}

	/** 获取元素能量 */
	public int getPower() {
		return power;
	}

	/** 获取元素的量 */
	public int getCount() {
		if (this.isEmpty()) return 0;
		return stackSize;
	}

	/** 设置元素的量 */
	public void setCount(int size) {
		stackSize = size;
		if (stackSize < 0) stackSize = 0;
	}

	/** 设置元素能量 */
	public void setPower(int power) {
		this.power = power;
	}

	/** 增长数量 */
	public void rise(float rate) {
		grow((int) (getCount() * rate));
	}

	/** 增长数量 */
	public void grow(int size) {
		setCount(getCount() + size);
	}

	/** 减少 */
	public void shrink(int size) {
		this.grow(-size);
	}

	/** 分割 */
	public ElementStack splitStack(int size) {
		if (this.isEmpty()) return ElementStack.EMPTY;
		ElementStack estack = this.copy();
		this.shrink(size);
		estack.setCount(Math.min(size, estack.getCount()));
		return estack;
	}

	/** 增长一个 estack */
	public void grow(ElementStack estack) {
		if (estack.isEmpty()) return;
		if (!this.areSameType(estack)) return;
		this.grow(estack.getCount());
		// 加权平均的能量
		int total = estack.getCount() + this.getCount();
		total = total == 0 ? 1 : total;
		this.setPower((estack.getPower() * estack.getCount() + this.getPower() * this.getCount()) / total);
	}

	/** 增长或成为一个 estack */
	public void growOrBecome(ElementStack estack) {
		if (this.isEmpty()) {
			if (estack.isEmpty()) return;
			if (this == ElementStack.EMPTY) return;
			this.become(estack);
		} else this.grow(estack);
	}

	/** 削弱 */
	public void weaken(float rate) {
		this.setPower(MathHelper.ceil(getPower() * rate));
	}

	/** 是否为空 */
	public boolean isEmpty() {
		Element element = this.getElement();
		if (element == EMPTY.element) return true;
		else if (element != null) {
			if (stackSize <= 0) return true;
			return false;
		} else return true;
	}

	/** 设置为空 */
	public void setEmpty() {
		this.become(EMPTY);
	}

	/** 是否为魔力元素 */
	public boolean isMagic() {
		return getElement() == ESObjects.ELEMENTS.MAGIC;
	}

	/** 是否为空 */
	public boolean isVoid() {
		return this.isEmpty();
	}

	/** 是否为同一个类型 */
	public boolean areSameType(ElementStack estack) {
		if (getElement() != estack.getElement()) return false;
		return true;
	}

	public boolean areSameEqual(ElementStack estack) {
		if (this.isEmpty()) return estack.isEmpty();
		if (!this.areSameType(estack)) return false;
		return this.getCount() == estack.getCount() && this.getPower() == estack.getPower();
	}

	/** 是否强于 */
	public boolean arePowerfulThan(ElementStack estack) {
		if (this.isEmpty()) return false;
		else if (estack.isEmpty()) return true;
		else if (!this.areSameType(estack)) return false;
		return this.getPower() >= estack.getPower();
	}

	/** 是否强于并且数量多于 */
	public boolean arePowerfulAndMoreThan(ElementStack estack) {
		return this.getCount() >= estack.getCount() && this.arePowerfulThan(estack);
	}

	/** 获取元素名称 */
	public String getElementUnlocalizedName() {
		return getElement().getUnlocalizedName(this) + ".name";
	}

	@SideOnly(Side.CLIENT)
	public String getDisplayName() {
		return I18n.format(this.getElementUnlocalizedName());
	}

	public ITextComponent getTextComponent() {
		ITextComponent itextcomponent = new TextComponentString("[");
		itextcomponent.appendSibling(new TextComponentTranslation(getElement().getUnlocalizedName(this) + ".name"));
		itextcomponent.appendSibling(new TextComponentString("]"));
		return itextcomponent;
	}

	@Override
	public String toString() {
		return getCount() + "x" + this.getElement().getUnlocalizedName(this) + ":" + getPower();
	}

	/** 物品被析构成元素的时候回调 */
	public ElementStack becomeElementWhenDeconstruct(World world, ItemStack stack, int complex, int lvPower) {
		return getElement().changetoElementWhenDeconstruct(world, stack, this, complex, lvPower);
	}

	/** 转化成元素时候回调 */
	public ElementStack becomeMagic(@Nullable World world) {
		if (this.isMagic()) return this;
		if (this.isEmpty()) return this;
		ElementStack magic = getElement().changetoMagic(world, this);
		magic.setElement(ESObjects.ELEMENTS.MAGIC);
		return magic;
	}

	public ElementStack toMagic(@Nullable World world) {
		return this.copy().becomeMagic(world);
	}

	public void setTagCompound(@Nullable NBTTagCompound nbt) {
		this.stackTagCompound = nbt;
	}

	public NBTTagCompound getTagCompound() {
		return stackTagCompound;
	}

	public NBTTagCompound getOrCreateTagCompound() {
		if (stackTagCompound == null) stackTagCompound = new NBTTagCompound();
		return stackTagCompound;
	}

	/** 序列化 */
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return nbt;
	}

	/** 反序列化 */
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// 读取元素类型
		ResourceLocation name = new ResourceLocation(nbt.getString("id"));
		this.setElement(Element.getElementFromName(name));
		if (getElement() == null) this.setElement(EMPTY.element);
		// 读取元素数量
		this.setCount(nbt.getInteger("size"));
		// 读取元素能量
		this.setPower(nbt.getInteger("power"));
		// 自定义数据
		if (nbt.hasKey("tag", NBTTag.TAG_COMPOUND)) this.setTagCompound(nbt.getCompoundTag("tag"));
	}

	/** 序列化 */
	public void writeToNBT(NBTTagCompound nbt) {
		// 写入元素类型
		ResourceLocation resourcelocation = Element.getNameFromElement(getElement());
		nbt.setString("id", resourcelocation == null ? EMPTY.getElement().getRegistryName().toString()
				: resourcelocation.toString());
		// 写入元素的数量
		nbt.setInteger("size", getCount());
		// 写入元素的能量
		nbt.setInteger("power", getPower());
		// 自定义数据
		if (stackTagCompound != null && !stackTagCompound.hasNoTags()) nbt.setTag("tag", stackTagCompound);
	}

}
