package yuzunyannn.elementalsorcery.crafting.element;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

//默认的实例化
public class DefaultToElement implements IToElement {

	public Map<Item, List<ElementInfo>> stackToElementMap = new IdentityHashMap<Item, List<ElementInfo>>();
	public Map<Item, ElementInfo> itemToElementMap = new IdentityHashMap<Item, ElementInfo>();

	public void add(ItemStack stack, int complex, ItemStack[] remains, ElementStack... estacks) {
		boolean checkNBT = false;
		// 如果存在"checkNBTTag"字段，则添加标签，这个标签只是标记用的，所以要删除
		if (stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt.hasKey("checkNBTTag")) {
				checkNBT = true;
				nbt.removeTag("checkNBTTag");
			}
		}
		List<ElementInfo> infoList = stackToElementMap.get(stack.getItem());
		if (infoList == null) stackToElementMap.put(stack.getItem(), infoList = new ArrayList<ElementInfo>());
		// 检查是否存在
		for (int i = 0; i < infoList.size(); i++) {
			ElementInfo info = infoList.get(i);
			// 有相同的就替换
			if (ItemStack.areItemsEqual(info.stack, stack)) {
				if (checkNBT && !ItemStack.areItemStackTagsEqual(info.stack, stack)) continue;
				infoList.set(i, new ElementInfo(stack, estacks, complex));
				return;
			}
		}
		boolean hasNoRemains = remains == null || remains.length <= 0;
		if (hasNoRemains) infoList.add(new ElementInfo(stack, estacks, complex));
		else infoList.add(new ElementInfoRemain(stack, estacks, complex, remains));
	}

	public void add(Item item, int complex, ItemStack[] remains, ElementStack... estacks) {
		boolean hasNoRemains = remains == null || remains.length <= 0;
		if (hasNoRemains) itemToElementMap.put(item, new ElementInfo(ItemStack.EMPTY, estacks, complex));
		else itemToElementMap.put(item, new ElementInfoRemain(ItemStack.EMPTY, estacks, complex, remains));
	}

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		IToElementInfo toElement = _toElement(stack);
		if (toElement == null) return null;
		if (stack.isItemDamaged() && stack.getItem().isRepairable()) {
			int maxDamage = stack.getMaxDamage();
			int damage = stack.getItemDamage();
			if (damage == 0) return toElement;
			float r = (maxDamage - damage) / (float) maxDamage;
			ElementStack[] elements = ElementHelper.dropElements(toElement.element(), r);
			if (elements == null || elements.length == 0) return null;
			return ToElementInfoStatic.create(toElement.complex(), toElement.remain(), elements);
		}
		return toElement;
	}

	private IToElementInfo _toElement(ItemStack stack) {
		List<ElementInfo> itemList = stackToElementMap.get(stack.getItem());
		if (itemList != null) {
			for (ElementInfo info : itemList) {
				if (ItemHelper.areItemsEqual(info.stack, stack)) return info;
			}
		}
		return this.toElement(stack.getItem());
	}

	public IToElementInfo toElement(Item item) {
		if (itemToElementMap.containsKey(item)) return itemToElementMap.get(item);
		return null;
	}

	protected void merge(DefaultToElement other) {
		for (Entry<Item, ElementInfo> entry : other.itemToElementMap.entrySet()) {
			if (this.itemToElementMap.containsKey(entry.getKey())) continue;
			this.itemToElementMap.put(entry.getKey(), entry.getValue());
		}
		for (Entry<Item, List<ElementInfo>> entry : other.stackToElementMap.entrySet()) {
			List<ElementInfo> otherInfoList = entry.getValue();
			List<ElementInfo> infoList = this.stackToElementMap.get(entry.getKey());
			if (infoList == null) this.stackToElementMap.put(entry.getKey(), infoList = new ArrayList<ElementInfo>());
			for (ElementInfo info : otherInfoList) {
				if (this.toElement(info.stack) != null) continue;
				infoList.add(info);
			}
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

	static public class ElementInfoRemain extends ElementInfo {

		final public ItemStack[] remains;

		public ElementInfoRemain(ItemStack stack, ElementStack[] estacks, int complex, ItemStack... remains) {
			super(stack, estacks, complex);
			this.remains = remains;
		}

		@Override
		public ItemStack[] remain() {
			return remains;
		}

	}

}
