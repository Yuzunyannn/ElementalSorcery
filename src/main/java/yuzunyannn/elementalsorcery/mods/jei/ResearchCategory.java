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
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.crafting.IResearchRecipe;

public class ResearchCategory implements IRecipeCategory<ResearchRecipeWrapper> {

	public static final String UID = ElementalSorcery.MODID + "." + "research";
	private final ResearchDraw background = new ResearchDraw();

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
		return ElementalSorcery.MODID;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ResearchRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IResearchRecipe r = recipeWrapper.getRecipe();
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		NonNullList<Ingredient> list = r.getIngredients();
		int i = 0;
		for (Ingredient ingredient : list) {

			ItemStack[] stack = ingredient.getMatchingStacks();
			if (stack == null || stack.length == 0) continue;

			group.init(i, true, i * 18, 120);
			group.set(i, Arrays.asList(stack));

			i++;
		}
		group.init(i, false, 72, 58);
		group.set(i, r.getRecipeOutput());
		background.setRecipe(r);
	}

}
