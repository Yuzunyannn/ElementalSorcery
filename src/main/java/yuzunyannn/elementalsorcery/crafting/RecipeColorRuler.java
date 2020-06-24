package yuzunyannn.elementalsorcery.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.DyeUtils;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemMagicRuler;

public class RecipeColorRuler extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe>
		implements IRecipe {

	final ItemStack output = new ItemStack(ESInitInstance.ITEMS.MAGIC_RULER);
	final Item wantItem = ESInitInstance.ITEMS.MAGIC_RULER;
	final NonNullList<Ingredient> ingredient = NonNullList.create();

	public RecipeColorRuler() {
		ingredient.add(Ingredient.fromItems(ESInitInstance.ITEMS.MAGIC_RULER));
		NonNullList<ItemStack> list = OreDictionary.getOres("dye");
		ingredient.add(Ingredient.fromStacks(list.toArray(new ItemStack[list.size()])));
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredient;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		boolean hasWant = false;
		boolean hasColor = false;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == wantItem) {
				if (hasWant) return false;
				hasWant = true;
			} else if (DyeUtils.isDye(stack)) {
				if (hasColor) return false;
				hasColor = true;
			} else return false;

		}
		return hasWant && hasColor;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack origin = this.findWant(inv);
		ItemStack stack = origin.copy();
		return ItemMagicRuler.setColor(stack, this.findColor(inv));
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 2 && height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	private EnumDyeColor findColor(InventoryCrafting inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (DyeUtils.isDye(stack)) { return EnumDyeColor.byMetadata(DyeUtils.metaFromStack(stack).getAsInt()); }
		}
		return null;
	}

	private ItemStack findWant(InventoryCrafting inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == wantItem) { return stack; }
		}
		return ItemStack.EMPTY;
	}

}
