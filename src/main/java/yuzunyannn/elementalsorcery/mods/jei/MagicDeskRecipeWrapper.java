package yuzunyannn.elementalsorcery.mods.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;

public class MagicDeskRecipeWrapper implements IRecipeWrapper {

	final TileMagicDesk.Recipe recipe;

	public MagicDeskRecipeWrapper(TileMagicDesk.Recipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(ItemStack.class, recipe.getInput());
		ingredients.setInputs(ItemStack.class, recipe.getSequence());
		ingredients.setOutput(ItemStack.class, recipe.getOutput());
	}
	
	public TileMagicDesk.Recipe getRecipe() {
		return recipe;
	}

}
