package yuzunyannn.elementalsorcery.mods.jei;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
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
		IResearchRecipe r  = recipeWrapper.getRecipe();
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		group.init(0, false, 72, 68);
		group.set(0, r.getRecipeOutput());
		background.setRecipe(r);
	}

}
