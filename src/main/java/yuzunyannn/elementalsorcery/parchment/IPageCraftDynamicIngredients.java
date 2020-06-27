package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

public interface IPageCraftDynamicIngredients {

	public NonNullList<Ingredient> getIngredients(ItemStack output);
}
