package yuzunyannn.elementalsorcery.mods.jei;

import java.util.Arrays;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron.MeltCauldronRecipe;

public class MeltCauldronRecipeWrapper implements IRecipeWrapper {

	final MeltCauldronRecipe recipe;

	public MeltCauldronRecipeWrapper(MeltCauldronRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		for (Ingredient ingredient : recipe.getNeedList())
			ingredients.setInput(ItemStack.class, Arrays.asList(ingredient.getMatchingStacks()));
		ingredients.setOutput(ItemStack.class, recipe.getResultList());
	}

	public MeltCauldronRecipe getRecipe() {
		return recipe;
	}

}
