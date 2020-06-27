package yuzunyannn.elementalsorcery.mods.jei;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.crafting.RecipeRiteWrite;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;

public class RiteCategory implements IRecipeCategory<RiteRecipeWrapper> {

	public static final String UID = ElementalSorcery.MODID + "." + "rite";
	private final IDrawable background;

	public RiteCategory() {
		background = ESJEIPlugin.guiHelper.createDrawable(
				new ResourceLocation(ElementalSorcery.MODID, "textures/gui/rite_craft.png"), 0, 0, 148, 60);
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("page.riteCraft");
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
	public void setRecipe(IRecipeLayout recipeLayout, RiteRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		TileRiteTable.Recipe r = recipeWrapper.getRecipe();
		group.init(0, true, 4, 3);
		group.set(0, new ItemStack(Items.FEATHER));
		group.init(1, true, 4 + 18, 3);
		group.set(1, new ItemStack(Items.DYE));
		group.init(2, true, 4, 3 + 18);
		group.set(2, new ItemStack(ESInitInstance.ITEMS.PARCHMENT));
		group.init(3, true, 4 + 18, 3 + 18);
		group.set(3, r.parchmentInput());

		ItemStack stack = new ItemStack(ESInitInstance.ITEMS.PARCHMENT);
		RecipeRiteWrite.setInnerStack(stack, r.parchmentInput());
		group.init(4, true, 4 + 18 + 18 + 42, 3 + 18);
		group.set(4, stack);

		group.init(5, true, 4 + 18 + 18 + 42 + 44, 3 + 18);
		group.set(5, r.getOutput());
	}

}
