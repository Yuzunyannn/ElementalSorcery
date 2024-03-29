package yuzunyannn.elementalsorcery.api.tile;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

/** 物品结构合成，记录物品的结构 */
public interface IItemStructureCraft {

	/** 获得所有输入 */
	@Nullable
	Collection<ItemStack> getInputs();

	/** 获取强制附加的元素 */
	@Nullable
	default ElementStack[] getExtraElements() {
		return null;
	}

	/** 强制提升的复杂度 */
	default int getComplexIncr() {
		return 0;
	}

	/** 获取输出，输出仅允许有一个 */
	@Nonnull
	ItemStack getOutput();

	/** 是否需要到解析到底 */
	default Collection<ItemStack> getRemains() {
		return null;
	}

}
