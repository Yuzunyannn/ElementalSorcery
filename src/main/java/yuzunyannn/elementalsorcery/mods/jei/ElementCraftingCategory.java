package yuzunyannn.elementalsorcery.mods.jei;

import java.util.Arrays;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.crafting.IElementRecipe;
import yuzunyannn.elementalsorcery.container.ContainerSupremeTable;

public class ElementCraftingCategory implements IRecipeCategory<ElementCraftingRecipeWrapper> {

	public static final String UID = ESAPI.MODID + "." + "crafting";
	private final ElementCraftingDraw background = new ElementCraftingDraw();

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("info.elementCrafting");
	}

	@Override
	public String getModName() {
		return ESAPI.MODID;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ElementCraftingRecipeWrapper recipeWrapper,
			IIngredients ingredients) {
		IElementRecipe recipe = recipeWrapper.getRecipe();
		int xoff = 9 - 35;
		int yoff = -1 - 21;
		NonNullList<Ingredient> list = recipe.getIngredients();
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		final int size = ContainerSupremeTable.craftingRelative.length / 2;
		for (int i = 0; i < size; i++) {
			int x = ContainerSupremeTable.craftingRelative[i * 2];
			int y = ContainerSupremeTable.craftingRelative[i * 2 + 1];
			group.init(i, true, xoff + x + 88, yoff + y + 57);
			if (i < list.size()) {
				ItemStack[] stack = list.get(i).getMatchingStacks();
				if (stack == null || stack.length == 0) continue;
				group.set(i, Arrays.asList(stack));
			} else break;
		}
		group.init(size, false, xoff + 106, yoff + 129);
		group.set(size, ingredients.getOutputs(ItemStack.class).get(0));
		background.setElementList(recipe.getNeedElements());
	}

}
