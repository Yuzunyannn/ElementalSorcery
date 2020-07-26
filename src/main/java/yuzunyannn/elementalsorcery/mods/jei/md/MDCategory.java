package yuzunyannn.elementalsorcery.mods.jei.md;

import java.util.List;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class MDCategory<T extends MDRecipeWrapper> implements IRecipeCategory<T> {

	final String UID;
	final String name;
	private final MDDraw background = new MDDraw();

	public MDCategory(String id) {
		int i = id.lastIndexOf('.');
		if (i == -1) name = id;
		else name = id.substring(i + 1);
		UID = id;
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tile." + name + ".name");
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
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return background.getTooltipStrings(mouseX, mouseY);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, T recipeWrapper, IIngredients ingredients) {
		recipeWrapper.layout(recipeLayout);
		background.setMDCategory(recipeWrapper);
	}

}
