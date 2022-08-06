package yuzunyannn.elementalsorcery.mods.jei;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import yuzunyannn.elementalsorcery.api.ESAPI;

public class DescribeCategory implements IRecipeCategory<DescribeRecipeWrapper> {

	public static final String UID = ESAPI.MODID + "." + "describe";
	private final DescribeDraw background = new DescribeDraw();

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return background.getTitle();
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
	public IDrawable getIcon() {
		return background.icon;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, DescribeRecipeWrapper recipeWrapper, IIngredients ingredients) {
		background.setDrw(recipeWrapper);
	}

}
