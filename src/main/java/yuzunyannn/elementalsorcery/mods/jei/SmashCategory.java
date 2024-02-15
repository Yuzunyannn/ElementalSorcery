package yuzunyannn.elementalsorcery.mods.jei;

import java.util.List;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.api.ESAPI;

public class SmashCategory implements IRecipeCategory<SmashRecipeWrapper> {

	public static final String UID = ESAPI.MODID + "." + "millHammer";
	private final IDrawable background;

	public SmashCategory() {
		background = ESJEIPlugin.guiHelper
				.createDrawable(new ResourceLocation(ESAPI.MODID, "textures/gui/jei/mill_hammer.png"), 0, 0, 116, 74);
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("item.millHammer.name");
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
	public void setRecipe(IRecipeLayout recipeLayout, SmashRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<ItemStack> outputs = recipeWrapper.getRecipe().getOutputs();

		group.init(0, true, 1, 28);
		group.set(0, inputs.get(0));

		int xoffset = 43;
		int yoffset = 1;

		for (int i = 0; i < 16; i++) {
			int x = (i % 4) * 18;
			int y = (i / 4) * 18;
			if (x < 0) x = 5 + x;
			group.init(1 + i, false, xoffset + x, yoffset + y);
			if (i < outputs.size()) group.set(1 + i, outputs.get(i));
		}
	}

}
