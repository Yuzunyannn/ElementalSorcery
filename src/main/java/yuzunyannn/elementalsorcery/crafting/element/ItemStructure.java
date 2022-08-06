package yuzunyannn.elementalsorcery.crafting.element;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.event.ESEvent;
import yuzunyannn.elementalsorcery.api.event.EventGetItemStructure;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemStructure implements IItemStructure {

	static public IItemStructure getItemStructure(ItemStack stack) {
		if (stack.isEmpty()) return new ItemStructure(ItemStack.EMPTY);
		EventGetItemStructure event = ESEvent.post(new EventGetItemStructure(stack));
		if (event.isCanceled()) return new ItemStructure(ItemStack.EMPTY);
		IItemStructure itemStructure = event.getItemStructure();
		return itemStructure == null ? new ItemStructure(stack) : itemStructure;
	}

	static public boolean canStorageItemStructure(ItemStack stack) {
		return stack.getItem() == ESObjects.ITEMS.ITEM_CRYSTAL || stack.getSubCompound("istru") != null;
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
	public boolean hasState(NBTTagCompound nbt) {
		nbt = nbt.getCompoundTag("istru");
		if (!nbt.hasKey("els", 9)) return false;
		if (!nbt.hasKey("item", 10)) return false;
		return true;
	}

	@Override
	public void loadState(NBTTagCompound nbt) {
		nbt = nbt.getCompoundTag("istru");
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
	public void saveState(NBTTagCompound nbt) {
		if (this.stack.isEmpty()) return;
		if (!nbt.hasKey("istru", 10)) {
			NBTTagCompound _new = new NBTTagCompound();
			nbt.setTag("istru", _new);
			nbt = _new;
		} else nbt = nbt.getCompoundTag("istru");
		nbt.setTag("item", this.stack.serializeNBT());
		NBTTagList list = new NBTTagList();
		for (ElementStack estack : estacks) list.appendTag(estack.serializeNBT());
		nbt.setTag("els", list);
		nbt.setInteger("complex", this.complex);
	}

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		if (ItemHelper.areItemsEqual(this.stack, stack)) return ToElementInfoStatic.create(complex, estacks);
		return null;
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
		if (stack.isEmpty()) return false;
		return ItemHelper.areItemsEqual(this.stack, stack);
	}

	@Override
	public int getVacancy() {
		return stack.isEmpty() ? 0 : -1;
	}
}
