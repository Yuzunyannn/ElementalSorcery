package yuzunyannn.elementalsorcery.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/** 从物品更新状态[由于物品的cap不会自动同步！！！不会自动同步！！] （当然，这个接口不仅仅作用在mc的能力系统上） */
public interface IItemCapbiltitySyn {

	/**
	 * 检测stack的标签是否拥有能力
	 * 
	 * @return 是否加载成功，ture为成功
	 */
	default boolean hasState(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return false;
		return this.hasState(nbt);
	}

	boolean hasState(NBTTagCompound nbt);

	/**
	 * 从stack中读取数据
	 */
	default void loadState(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) this.loadState(nbt);
	}

	/** 将数据写入stack */
	default ItemStack saveState(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		this.saveState(nbt);
		return stack;
	}

	/** 将数据从nbt中读取 */
	void loadState(NBTTagCompound nbt);

	/** 将数据写入nbt */
	void saveState(NBTTagCompound nbt);
}
