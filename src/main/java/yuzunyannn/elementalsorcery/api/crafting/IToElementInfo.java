package yuzunyannn.elementalsorcery.api.crafting;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public interface IToElementInfo {

	/**
	 * 获取本转化信息中的元素
	 * 
	 * @return 返回的数组下标越小表明这元素会被优先获取（部分地方可能只会获取0位置的元素）<br>
	 *         非特殊情况请不要修改返回值!!!!!!入要修改，请使用{@link ElementHelper#copy}
	 */
	@Nonnull
	ElementStack[] element();

	/** 获取复杂度 */
	int complex();

	/** 获取该物品转化成元素后，可能返回的内容，返回的内容应当不用copy，被允许直接使用 */
	default ItemStack remain() {
		return ItemStack.EMPTY;
	}

}
