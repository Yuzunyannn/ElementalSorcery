package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;

public class TutorialCraftMC extends TutorialCraft {

	static public TutorialCraftMC tryCreate(ItemStack itemStack) {
		List<IRecipe> allRecipe = new LinkedList();
		for (IRecipe ire : CraftingManager.REGISTRY) {
			if (isItemStackThinkSame(itemStack, ire.getRecipeOutput())) allRecipe.add(ire);
		}
		if (allRecipe.isEmpty()) return null;
		return new TutorialCraftMC(allRecipe, itemStack);
	}

	protected List<Entry<List<Ingredient>, ItemStack>> list = new ArrayList<>();

	public TutorialCraftMC(List<IRecipe> allRecipe, ItemStack output) {
		for (IRecipe ire : allRecipe) {
			List<Ingredient> ingredients;
			if (ire instanceof ITutorialCraftDynamicIngredients) {
				ingredients = ((ITutorialCraftDynamicIngredients) ire).getIngredients(output);
			} else {
				ingredients = ire.getIngredients();
				output = ire.getRecipeOutput();
			}
			if (ingredients.isEmpty()) continue;
			list.add(entryOf(ingredients, output));
		}
	}

	@Override
	public GShowCommon createMyContainer(TutorialCraftNodeParams params) {
		return new GShow(params);
	}

	protected class GShow extends GShowCommon {

		protected List<GItemFrame> inputs = new ArrayList<>();
		protected GItemFrame output;

		public GShow(TutorialCraftNodeParams params) {
			super(params);
			initCraft(params, Blocks.CRAFTING_TABLE);
			
			double xOffset = 20;
			double padOffsetX = -35 + xOffset;
			double padOffsetY = 9;
			for (int r = 0; r < 3; r++) {
				for (int c = 0; c < 3; c++) {
					inputs.add(addSlot(c * 18 - 18 * 3 / 2 + padOffsetX, r * 18 - 18 * 3 / 2 + padOffsetY));
				}
			}
			addArrow(xOffset, 0);
			output = addSlot(25 + xOffset, 0);
		}

		@Override
		protected Collection<?> getElements() {
			return list;
		}

		@Override
		public void updateCraft() {
			if (list.isEmpty()) return;
			Entry<List<Ingredient>, ItemStack> recipe = list.get(showIndex);
			output.setItemStack(recipe.getValue());
			List<Ingredient> list = recipe.getKey();
			for (int i = 0; i < inputs.size(); i++) {
				if (i < list.size()) inputs.get(i).setItemStack(getItemStack(list.get(i)));
				else inputs.get(i).setItemStack(ItemStack.EMPTY);
			}
		}

	}

}
