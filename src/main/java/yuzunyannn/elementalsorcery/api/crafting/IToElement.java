package yuzunyannn.elementalsorcery.api.crafting;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public interface IToElement {

	/**
	 * 将物品栈转化成元素
	 * 
	 * @param stack 传入的物品栈
	 * @return 返回一组转变的元素，注意：这里的返回值应与stack的数量无关。返回的数组下标越小表明这元素会被优先获取（部分地方可能只会获取0位置的元素）<br>
	 *         非特殊情况请不要修改返回值!!!!!!入要修改，请使用{@link ElementHelper#copy}
	 */
	@Nullable
	ElementStack[] toElement(ItemStack stack);

	/** 获取复杂度，返回小于等于0，表示失败 */
	int complex(ItemStack stack);

	/** 获取该物品转化成元素后，可能返回的内容，返回的内容应当不用copy，被允许直接使用 */
	default ItemStack remain(ItemStack stack) {
		return ItemStack.EMPTY;
	}
}
