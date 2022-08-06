package yuzunyannn.elementalsorcery.api.crafting;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public interface IResearchRecipe {

	/** 是否匹配 */
	boolean matches(IResearcher researcher, World worldIn);

	/**
	 * 获取匹配权重，所有matches满足条件的合成表，获取的权重会进行相对处理，最小的会归为1</br>
	 * 也就是说，无论这里返回啥值，都有可能成为最终选择的内容</br>
	 * 推荐返回负值，表示偏差
	 */
	float getMatchWeight(IResearcher researcher, List<IResearchRecipe> qualified, World worldIn);

	/** 获取所需的物品表，物品表内物品会被扣除 */
	NonNullList<Ingredient> getIngredients();

	/** 合成表结果 */
	default ItemStack getRecipeOutput(IResearcher researcher) {
		return this.getRecipeOutput();
	}

	ItemStack getRecipeOutput();

	/** 需要的知识与数量，作为显示 */
	List<Entry<String, Integer>> getRecipeInput();

}
