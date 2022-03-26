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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron.MeltCauldronRecipe;

public class MeltCauldronCategory implements IRecipeCategory<MeltCauldronRecipeWrapper> {

	public static final String UID = ElementalSorcery.MODID + "." + "meltCauldron";
	private final IDrawable background;

	public MeltCauldronCategory() {
		background = ESJEIPlugin.guiHelper.createDrawable(
				new ResourceLocation(ElementalSorcery.MODID, "textures/gui/jei/melt_cauldron_jei.png"), 0, 0, 162, 88);
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tile.meltCauldron.name");
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
	public void setRecipe(IRecipeLayout recipeLayout, MeltCauldronRecipeWrapper recipeWrapper,
			IIngredients ingredients) {
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		MeltCauldronRecipe r = recipeWrapper.getRecipe();

		int i = 0;
		for (Ingredient ingredient : r.getNeedList()) {
			group.init(i, true, 2 + i * 20, 0);
			group.set(i, Arrays.asList(ingredient.getMatchingStacks()));
			i++;
		}

		group.init(i, true, 72, 22);
		group.set(i, new ItemStack(ESInit.ITEMS.MAGIC_STONE, r.getMagicStoneCount()));
		i++;

		group.init(i, false, 72, 53);
		group.set(i, r.getResultList());

		final int lastSlotIndex = i;

		group.addTooltipCallback((slotIndex, input, stack, tooltip) -> {
			if (slotIndex == lastSlotIndex) {
				r.ergodicResult(entry -> {
					if (ItemStack.areItemsEqual(entry.getValue(), stack)
							&& entry.getValue().getCount() == stack.getCount()) {
						tooltip.add(
								TextFormatting.YELLOW + I18n.format("info.standardDeviation") + " < " + entry.getKey());
					}
				});
			}
		});
	}

}
