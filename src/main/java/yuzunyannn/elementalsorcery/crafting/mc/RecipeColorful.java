package yuzunyannn.elementalsorcery.crafting.mc;

import java.util.function.BiFunction;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.DyeUtils;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeColorful extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	protected final ItemStack output;
	protected final ItemStack wantStack;
	protected final NonNullList<Ingredient> ingredient = NonNullList.create();
	protected BiFunction<ItemStack, EnumDyeColor, ItemStack> setter = (stack, color) -> stack;

	public RecipeColorful(ItemStack wantStack) {
		this.wantStack = wantStack;
		this.output = wantStack.copy();

		ingredient.add(Ingredient.fromItems(this.wantStack.getItem()));
		NonNullList<ItemStack> list = OreDictionary.getOres("dye");
		ingredient.add(Ingredient.fromStacks(list.toArray(new ItemStack[list.size()])));
	}

	public RecipeColorful colorSetter(BiFunction<ItemStack, EnumDyeColor, ItemStack> setter) {
		this.setter = setter;
		return this;
	}

	@Override
	public boolean isDynamic() {
		return true;
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
			if (ItemStack.areItemsEqual(stack, wantStack)) {
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
		return setter.apply(stack, findColor(inv));
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
			if (DyeUtils.isDye(stack)) return EnumDyeColor.byMetadata(DyeUtils.metaFromStack(stack).getAsInt());
		}
		return null;
	}

	private ItemStack findWant(InventoryCrafting inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (ItemStack.areItemsEqual(stack, wantStack)) return stack;
		}
		return ItemStack.EMPTY;
	}

}
