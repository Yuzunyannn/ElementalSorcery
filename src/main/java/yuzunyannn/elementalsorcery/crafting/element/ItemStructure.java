package yuzunyannn.elementalsorcery.crafting.element;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemStructure implements IItemStructure {

	static public IItemStructure getItemStructure(ItemStack stack) {
		// 这里应当加入获取ItemStructure[事件]
		return new ItemStructure(stack);
	}

	
	
	static public boolean canStorageItemStructure(ItemStack stack) {
		return stack.getItem() == ESInitInstance.ITEMS.ITEM_CRYSTAL || stack.getSubCompound("istru") != null;
	}

	ItemStack stack = ItemStack.EMPTY;
	int complex;
	ElementStack[] estacks;

	public ItemStructure() {

	}

	public ItemStructure(ItemStack stack) {
		this.loadState(stack);
	}

	public boolean isInvalid() {
		return stack.isEmpty() || estacks == null;
	}

	@Override
	public void set(int index, ItemStack stack, int complex, ElementStack... estacks) {
		this.stack = stack;
		this.estacks = estacks;
		this.complex = complex;
		if (this.complex <= 0) this.complex = ElementHelper.getComplexFromElements(this.stack, this.estacks);
	}

	@Override
	public void add(ItemStack stack, int complex, ElementStack... estacks) {
		this.set(0, stack, complex, estacks);
	}

	@Override
	public boolean hasState(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("istru");
		if (nbt == null) return false;
		if (!nbt.hasKey("els", 9)) return false;
		if (!nbt.hasKey("item", 10)) return false;
		return true;
	}

	@Override
	public void loadState(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("istru");
		if (nbt == null) {
			this.stack = ItemStack.EMPTY;
			this.estacks = null;
			return;
		}
		this.stack = new ItemStack(nbt.getCompoundTag("item"));
		if (this.stack.isEmpty()) {
			estacks = null;
			return;
		}
		NBTTagList list = nbt.getTagList("els", 10);
		estacks = new ElementStack[list.tagCount()];
		for (int i = 0; i < estacks.length; i++) estacks[i] = new ElementStack(list.getCompoundTagAt(i));
		this.complex = nbt.getInteger("complex");
		if (this.complex <= 0) this.complex = ElementHelper.getComplexFromElements(this.stack, this.estacks);
	}

	@Override
	public void saveState(ItemStack stack) {
		NBTTagCompound nbt = stack.getOrCreateSubCompound("istru");
		if (this.stack.isEmpty()) return;
		nbt.setTag("item", this.stack.serializeNBT());
		NBTTagList list = new NBTTagList();
		for (ElementStack estack : estacks) {
			list.appendTag(estack.serializeNBT());
		}
		nbt.setTag("els", list);
		nbt.setInteger("complex", this.complex);
	}

	@Override
	public ElementStack[] toElement(ItemStack stack) {
		if (ItemHelper.areItemsEqual(this.stack, stack)) return estacks;
		return null;
	}

	@Override
	public int complex(ItemStack stack) {
		if (ItemHelper.areItemsEqual(this.stack, stack)) return complex;
		return 0;
	}

	@Override
	public int getItemCount() {
		return stack.isEmpty() ? 0 : 1;
	}

	@Override
	public int getItemMaxCount() {
		return 1;
	}

	@Override
	public ItemStack getStructureItem(int index) {
		return stack;
	}

	@Override
	public boolean hasItem(ItemStack stack) {
		return ItemHelper.areItemsEqual(this.stack, stack);
	}
}
