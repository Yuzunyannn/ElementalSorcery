package yuzunyannn.elementalsorcery.crafting.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

//默认的实例化
public class DefaultToElement implements IToElement {

	public List<ElementInfo> stackToElementMap = new ArrayList<ElementInfo>();
	public Map<Item, ElementInfo> itemToElementMap = new HashMap<Item, ElementInfo>();

	public void add(ItemStack stack, int complex, ElementStack... estacks) {
		boolean checkNBT = false;
		// 如果存在"checkNBTTag"字段，则添加标签，这个标签只是标记用的，所以要删除
		if (stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt.hasKey("checkNBTTag")) {
				checkNBT = true;
				nbt.removeTag("checkNBTTag");
			}
		}
		// 检查是否存在
		for (int i = 0; i < stackToElementMap.size(); i++) {
			ElementInfo info = stackToElementMap.get(i);
			// 有相同的就替换
			if (ItemStack.areItemsEqual(info.stack, stack)) {
				if (checkNBT && !ItemStack.areItemStackTagsEqual(info.stack, stack)) continue;
				stackToElementMap.set(i, new ElementInfo(stack, estacks, complex));
				return;
			}
		}
		stackToElementMap.add(new ElementInfo(stack, estacks, complex));
	}

	public void add(Item item, int complex, ElementStack... estacks) {
		itemToElementMap.put(item, new ElementInfo(ItemStack.EMPTY, estacks, complex));
	}

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		for (ElementInfo info : this.stackToElementMap) {
			if (this.compareItemStacks(stack, info.stack)) return info;
		}
		return this.toElement(stack.getItem());
	}

	public IToElementInfo toElement(Item item) {
		if (itemToElementMap.containsKey(item)) return itemToElementMap.get(item);
		return null;
	}

	private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
		return ItemHelper.areItemsEqual(stack1, stack2);
	}

	protected void merge(DefaultToElement other) {
		for (Entry<Item, ElementInfo> entry : other.itemToElementMap.entrySet()) {
			if (this.itemToElementMap.containsKey(entry.getKey())) continue;
			this.itemToElementMap.put(entry.getKey(), entry.getValue());
		}
		for (ElementInfo info : other.stackToElementMap) {
			if (this.toElement(info.stack) != null) continue;
			this.stackToElementMap.add(info);
		}
	}

	static public class ElementInfo implements IToElementInfo {
		final public ItemStack stack;
		final public ElementStack[] estacks;
		final public int complex;

		public ElementInfo(ItemStack stack, ElementStack[] estacks, int complex) {
			this.stack = stack;
			this.estacks = estacks;
			this.complex = complex;
		}

		@Override
		public ElementStack[] element() {
			return estacks;
		}

		@Override
		public int complex() {
			return complex;
		}
	}

}
