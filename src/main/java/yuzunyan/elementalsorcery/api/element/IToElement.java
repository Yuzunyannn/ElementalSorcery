package yuzunyan.elementalsorcery.api.element;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IToElement {

	/**
	 * 将物品栈转化成元素
	 * 
	 * @param stack
	 *            传入的物品栈
	 * @return 返回一组转变的元素，注意：这里的返回值应与stack的数量无关。返回的数组下标越小表明这元素会被优先获取（部分地方可能只会获取0位置的元素），非特殊情况请不要修改返回值!
	 */
	@Nullable
	default ElementStack[] toElement(ItemStack stack) {
		return this.toElement(stack.getItem());
	}

	/** 将方块转成元素 */
	@Nullable
	default ElementStack[] toElement(Block block) {
		return this.toElement(Item.getItemFromBlock(block));
	}

	/** 将物品转化成元素 */
	@Nullable
	ElementStack[] toElement(Item item);

	/** 获取该物品转化成元素后，可能返回的内容 */
	default ItemStack remain(ItemStack stack) {
		return ItemStack.EMPTY;
	}
}
