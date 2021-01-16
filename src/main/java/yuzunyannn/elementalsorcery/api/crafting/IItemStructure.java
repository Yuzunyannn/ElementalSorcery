package yuzunyannn.elementalsorcery.api.crafting;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public interface IItemStructure extends IToElement, IItemCapbiltitySyn {

	/** 获取存放物品结构数目，通常只有1 */
	int getItemCount();

	/** 获取存放物品结构最大数目，通常只有1 */
	int getItemMaxCount();

	default boolean isEmpty() {
		return this.getItemCount() <= 0;
	}

	/**
	 * 获取使用，但实际转化的时候直接调用 toElement，获取仅仅作为一个参考
	 * 
	 * @param index 记录的第几个物品，其中0号物品作为主物品，作为逆向合成使用的物品
	 */
	ItemStack getStructureItem(int index);

	default boolean hasItem(ItemStack stack) {
		if (stack.isEmpty()) return false;
		for (int i = 0; i < this.getItemCount(); i++)
			if (ItemHelper.areItemsEqual(this.getStructureItem(i), stack)) return true;
		return false;
	}

	/** 设置使用 */
	public void set(int index, ItemStack stack, int complex, ElementStack... estacks);

	/** 添加使用 */
	public void add(ItemStack stack, int complex, ElementStack... estacks);
}
