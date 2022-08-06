package yuzunyannn.elementalsorcery.api.crafting;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public interface IToElement {

	/**
	 * 转化为元素，返回转化元素的信息
	 * 
	 * @return 返回转化的元素信息，注意：这里的返回值应与stack的数量无关。
	 */
	@Nullable
	public IToElementInfo toElement(ItemStack stack);

	/** 快捷处理 */
	default ElementStack[] toElementStack(ItemStack stack) {
		IToElementInfo teInfo = this.toElement(stack);
		return teInfo == null ? null : teInfo.element();
	}

}
