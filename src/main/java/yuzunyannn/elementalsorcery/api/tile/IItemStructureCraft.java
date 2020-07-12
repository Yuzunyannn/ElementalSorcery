package yuzunyannn.elementalsorcery.api.tile;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

/** 物品结构合成，记录物品的结构 */
public interface IItemStructureCraft {

	/** 获得所有输入 */
	@Nullable
	List<ItemStack> getInputs();

	/** 获取输出，输出仅允许有一个 */
	@Nonnull
	ItemStack getOutput();

	/** 是否需要到解析到底 */
	default boolean calcRemain(ItemStack input) {
		return ForgeHooks.getContainerItem(input).isEmpty();
	}

}
