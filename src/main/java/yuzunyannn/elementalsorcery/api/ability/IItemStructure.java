package yuzunyannn.elementalsorcery.api.ability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.IToElement;

public interface IItemStructure extends IToElement, IItemCapbiltitySyn {

	@Override
	default ElementStack[] toElement(Item item) {
		return this.toElement(new ItemStack(item));
	}

	@Override
	default int complex(Item item) {
		return this.complex(new ItemStack(item));
	}

	/** 获取存放物品结构数目，通常只有1 */
	int getItemCount();

	/**
	 * 获取使用，但实际转化的时候直接调用 toElement，获取仅仅作为一个参考
	 * 
	 * @param index
	 *            记录的第几个物品，其中0号物品作为主物品，作为逆向合成使用的物品
	 */
	ItemStack getStructureItem(int index);

	/** 设置使用 */
	public void set(int index, ItemStack stack, int complex, ElementStack... estacks);
}
