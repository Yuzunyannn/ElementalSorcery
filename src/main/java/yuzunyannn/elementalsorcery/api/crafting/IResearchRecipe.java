package yuzunyannn.elementalsorcery.api.crafting;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.researcher.Researcher;

public interface IResearchRecipe {

	/** 是否匹配 */
	boolean matches(Researcher researcher, World worldIn);

	/**
	 * 获取匹配权重，所有matches满足条件的合成表，获取的权重会进行相对处理，最小的会归为1</br>
	 * 也就是说，无论这里返回啥值，都有可能成为最终选择的内容</br>
	 * 推荐返回负值，表示偏差
	 */
	float getMatchWeight(Researcher researcher, List<IResearchRecipe> qualified, World worldIn);

	/** 合成表结果 */
	default ItemStack getRecipeOutput(Researcher researcher) {
		return this.getRecipeOutput();
	}

	ItemStack getRecipeOutput();

	/** 需要的知识与数量，作为显示 */
	List<Entry<String, Integer>> getRecipeInput();

}
