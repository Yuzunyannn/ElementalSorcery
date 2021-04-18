package yuzunyannn.elementalsorcery.api.tile;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import yuzunyannn.elementalsorcery.element.ElementStack;

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

	/** 获取输出，输出仅允许有一个 */
	@Nonnull
	ItemStack getOutput();

	/** 是否需要到解析到底 */
	default boolean calcRemain(ItemStack input) {
		return ForgeHooks.getContainerItem(input).isEmpty();
	}

}
