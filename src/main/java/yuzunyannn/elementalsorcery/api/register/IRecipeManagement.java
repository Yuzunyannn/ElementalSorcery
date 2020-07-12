package yuzunyannn.elementalsorcery.api.register;

import java.util.List;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;

public interface IRecipeManagement {
	void addRecipe(IRecipe recipe);

	void addRecipe(ItemStack output, Object... args);

	List<IRecipe> getRecipes();
}