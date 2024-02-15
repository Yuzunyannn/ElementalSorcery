package yuzunyannn.elementalsorcery.mods.jei;

import java.util.Arrays;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.crafting.ISmashRecipe;

public class SmashRecipeWrapper implements IRecipeWrapper {

	final ISmashRecipe recipe;

	public SmashRecipeWrapper(ISmashRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, Arrays.asList(recipe.getIngredient().getMatchingStacks()));
		ingredients.setOutputs(ItemStack.class, recipe.getOutputs());
	}

	public ISmashRecipe getRecipe() {
		return recipe;
	}

}
