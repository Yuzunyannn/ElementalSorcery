package yuzunyan.elementalsorcery.api.ability;

import net.minecraft.item.ItemStack;

/** 更新状态[由于物品的cap不会自动同步！！！不会自动同步！！] */
public interface IItemCapbiltitySyn {

	/**
	 * 检测stack的标签是否拥有能力
	 * 
	 * @return 是否加载成功，ture为成功
	 */
	boolean hasState(ItemStack stack);

	/**
	 * 从stack中读取数据
	 */
	void loadState(ItemStack stack);

	/** 将数据写入stack */
	void saveState(ItemStack stack);
}
