package yuzunyannn.elementalsorcery.mods.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;

public class RiteRecipeWrapper implements IRecipeWrapper {

	final TileRiteTable.Recipe recipe;

	public RiteRecipeWrapper(TileRiteTable.Recipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(ItemStack.class, recipe.parchmentInput());
		ingredients.setInput(ItemStack.class, new ItemStack(ESInitInstance.ITEMS.PARCHMENT));
		ingredients.setOutput(ItemStack.class, recipe.getOutput());
	}

	public TileRiteTable.Recipe getRecipe() {
		return recipe;
	}

}
