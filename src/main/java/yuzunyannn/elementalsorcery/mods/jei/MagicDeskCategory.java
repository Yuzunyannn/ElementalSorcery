package yuzunyannn.elementalsorcery.mods.jei;

import java.util.ArrayList;
import java.util.List;

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
import yuzunyannn.elementalsorcery.tile.altar.TileMagicDesk;

public class MagicDeskCategory implements IRecipeCategory<MagicDeskRecipeWrapper> {

	public static final String UID = ElementalSorcery.MODID + "." + "desk";
	private final IDrawable background;

	public MagicDeskCategory() {
		background = ESJEIPlugin.guiHelper.createDrawable(
				new ResourceLocation(ElementalSorcery.MODID, "textures/gui/container/magic_desk_jei.png"), 18, 18, 122,
				106);
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tile.magicDesk.name");
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
	public void setRecipe(IRecipeLayout recipeLayout, MagicDeskRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		TileMagicDesk.Recipe r = recipeWrapper.getRecipe();
		group.init(0, true, 33, 0);
		group.set(0, r.getInput());
		group.init(1, false, 75, 0);
		group.set(1, r.getOutput());
		List<ItemStack> list = r.getSequence();
		for (int i = 0; i < list.size(); i++) {
			int x = (i % 10) - 5;
			if (x < 0) x = 5 + x;
			else x = 4 - x;
			x = 26 * x;
			group.init(i + 2, true, x, 36 + 26 * (i / 5));
			group.set(i + 2, list.get(i));
		}
	}

}
