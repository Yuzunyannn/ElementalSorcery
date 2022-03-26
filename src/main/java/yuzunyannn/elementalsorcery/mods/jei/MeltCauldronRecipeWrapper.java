package yuzunyannn.elementalsorcery.mods.jei;

import java.util.ArrayList;
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
		ArrayList<ItemStack> list = new ArrayList<>();
		for (Ingredient ingredient : recipe.getNeedList()) list.addAll(Arrays.asList(ingredient.getMatchingStacks()));
		ingredients.setInputs(ItemStack.class, list);
		ingredients.setOutput(ItemStack.class, recipe.getResultList());
	}

	public MeltCauldronRecipe getRecipe() {
		return recipe;
	}

}
