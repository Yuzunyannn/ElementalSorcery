package yuzunyan.elementalsorcery.api.element;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;

public class ElementStack implements net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound> {

	public static ElementStack EMPTY;

	/** 元素数量 */
	private int stackSize;
	/** 元素 */
	private Element element;
	/** 元素能量 */
	private int power;

	private ElementStack() {

	}

	public ElementStack(Element element) {
		this(element, 1);
	}

	public ElementStack(Element element, int size) {
		this(element, size, 0);
	}

	public ElementStack(Element element, int size, int power) {
		this.element = element == null ? EMPTY.element : element;
		this.stackSize = size >= 0 ? size : 0;
		this.power = power >= 0 ? power : 0;
	}

	public ElementStack(NBTTagCompound compound) {
		this.deserializeNBT(compound);
	}

	public void become(ElementStack estack) {
		this.element = estack.element;
		this.stackSize = estack.stackSize;
		this.power = estack.power;
	}

	public ElementStack copy() {
		ElementStack itemstack = new ElementStack();
		itemstack.become(this);
		return itemstack;
	}

	@Override
	protected ElementStack clone() {
		return this.copy();
	}

	/** 获取元素 */
	public Element getElement() {
		return element;
	}

	/** 获取元素颜色 */
	public int getColor() {
		return element.getColor(this);
	}

	/** 获取元素能量 */
	public int getPower() {
		return power;
	}

	/** 获取元素的量 */
	public int getCount() {
		return stackSize;
	}

	/** 设置元素的量 */
	public void setCount(int size) {
		stackSize = size;
		if (stackSize < 0)
			stackSize = 0;
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
		if (this.isEmpty())
			return ElementStack.EMPTY;
		ElementStack estack = this.copy();
		this.shrink(size);
		estack.setCount(size);
		return estack;
	}

	/** 增长一个 estack */
	public void grow(ElementStack estack) {
		if (estack.isEmpty())
			return;
		if (!this.areSameType(estack))
			return;
		this.grow(estack.getCount());
		if (!this.usePower())
			return;
		// 加权平均的能量
		int total = estack.getCount() + this.getCount();
		total = total == 0 ? 1 : total;
		this.power = (estack.getPower() * estack.getCount() + this.getPower() * this.getCount()) / total;
	}

	/** 增长或成为一个 estack */
	public void growOrBecome(ElementStack estack) {
		if (this.isEmpty()) {
			if (estack.isEmpty())
				return;
			if (this == ElementStack.EMPTY)
				return;
			this.become(estack);
		} else
			this.grow(estack);
	}

	/** 削弱 */
	public void weaken(float rate) {
		if (this.usePower()) {
			this.power = (int) (this.power * rate);
		}
	}

	/** 是否为空 */
	public boolean isEmpty() {
		if (this.element == EMPTY.element)
			return true;
		else if (element != null) {
			if (stackSize <= 0)
				return true;
			return false;
		} else
			return true;
	}

	/** 设置为空 */
	public void setEmpty() {
		this.element = EMPTY.element;
		this.stackSize = 0;
	}

	/** 是否为空 */
	public boolean isVoid() {
		return this.isEmpty();
	}

	/** 是否启用能量 */
	public boolean usePower() {
		return element.usePower;
	}

	/** 是否为同一个类型 */
	public boolean areSameType(ElementStack estack) {
		if (this.element != estack.element)
			return false;
		if (!this.usePower() && this.power != estack.power)
			return false;
		return true;
	}

	/** 是否强于 */
	public boolean arePowerfulThan(ElementStack estack) {
		if (this.isEmpty())
			return false;
		else if (estack.isEmpty())
			return true;
		else if (!this.areSameType(estack))
			return false;
		return this.getPower() >= estack.getPower();
	}

	/** 是否强于并且数量多于 */
	public boolean arePowerfulAndMoreThan(ElementStack estack) {
		return this.getCount() >= estack.getCount() && this.arePowerfulThan(estack);
	}

	/**
	 * 物品被析构成元素的时候回调
	 */
	public ElementStack getElementWhenDeconstruct(ItemStack stack, int power) {
		return this.element.getElementWhenDeconstruct(stack, this, power);
	}

	public ElementStack getElementWhenDeconstruct(IBlockState state, int power) {
		return this.element.getElementWhenDeconstruct(
				new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)), this, power);
	}

	/** 获取元素名称 */
	public String getElementUnlocalizedName() {
		return this.getElement().getUnlocalizedName(this) + ".name";
	}

	/** 序列化 */
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		// 写入元素类型
		ResourceLocation resourcelocation = Element.getNameFromElement(this.element);
		nbt.setString("id", resourcelocation == null ? EMPTY.getElement().getRegistryName().toString()
				: resourcelocation.toString());
		// 写入元素的数量
		nbt.setInteger("size", stackSize);
		// 写入元素的能量
		nbt.setInteger("power", power);
		return nbt;
	}

	/** 反序列化 */
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// 读取元素类型
		ResourceLocation name = new ResourceLocation(nbt.getString("id"));
		element = Element.getElementFromName(name);
		element = element == null ? EMPTY.element : element;
		// 读取元素数量
		stackSize = nbt.getInteger("size");
		// 读取元素能量
		power = nbt.getInteger("power");
	}

	/** 能力 暂时未用 */
	private CapabilityDispatcher capabilities = null;

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capabilities == null ? false : capabilities.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capabilities == null ? null : capabilities.getCapability(capability, facing);
	}

}
