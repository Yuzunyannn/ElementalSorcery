package yuzunyannn.elementalsorcery.mods.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import yuzunyannn.elementalsorcery.api.crafting.IResearchRecipe;

public class ResearchRecipeWrapper implements IRecipeWrapper {

	final IResearchRecipe recipe;

	public ResearchRecipeWrapper(IResearchRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		IStackHelper stackHelper = ESJEIPlugin.stackHelper;
		NonNullList<Ingredient> list = recipe.getIngredients();
		ingredients.setInputs(ItemStack.class, stackHelper.toItemStackList(list));
		ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
	}

	public IResearchRecipe getRecipe() {
		return recipe;
	}

}
