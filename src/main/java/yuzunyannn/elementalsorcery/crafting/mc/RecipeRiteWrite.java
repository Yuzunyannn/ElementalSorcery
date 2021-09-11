package yuzunyannn.elementalsorcery.crafting.mc;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.parchment.IPageCraftDynamicIngredients;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;

public class RecipeRiteWrite extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe>
		implements IRecipe, IPageCraftDynamicIngredients {
	
	/** 获取物品内置物品 */
	public static ItemStack getInnerStack(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return ItemStack.EMPTY;
		return new ItemStack(nbt.getCompoundTag("riteItem"));
	}

	/** 设置物品内置物品 */
	public static void setInnerStack(ItemStack stack, ItemStack inner) {
		if (inner.isEmpty()) return;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		nbt.setTag("riteItem", inner.serializeNBT());
	}

	public RecipeRiteWrite() {

	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		int newItemIndex = -1;
		int dyeIndex = -1;
		int featherIndex = -1;
		int parchmentIndex = -1;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == Items.FEATHER) {
				if (featherIndex == -1) featherIndex = i;
				else return false;
			} else if (stack.getItem() == Items.DYE && stack.getItemDamage() == 0) {
				if (dyeIndex == -1) dyeIndex = i;
				else return false;
			} else if (stack.getItem() == ESInit.ITEMS.PARCHMENT) {
				if (parchmentIndex == -1) parchmentIndex = i;
				else return false;
			} else {
				if (newItemIndex == -1) newItemIndex = i;
				else return false;
			}
		}
		if (newItemIndex == -1 || dyeIndex == -1 || featherIndex == -1 || parchmentIndex == -1) return false;
		// ItemStack parchment = inv.getStackInSlot(parchmentIndex);
		ItemStack newItem = inv.getStackInSlot(newItemIndex);
		TileRiteTable.Recipe r = TileRiteTable.findRecipe(newItem);
		return r != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack parchment = ItemStack.EMPTY;
		ItemStack newItem = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == Items.FEATHER) continue;
			else if (stack.getItem() == Items.DYE && stack.getItemDamage() == 0) continue;
			else if (stack.getItem() == ESInit.ITEMS.PARCHMENT) parchment = stack;
			else newItem = stack;
		}
		parchment = parchment.copy();
		parchment.setCount(1);
		RecipeRiteWrite.setInnerStack(parchment, newItem);
		return parchment;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) list.set(i, ItemStack.EMPTY);
			else if (stack.getItem() == Items.FEATHER) list.set(i, ItemStack.EMPTY);
			else if (stack.getItem() == Items.DYE && stack.getItemDamage() == 0) list.set(i, ItemStack.EMPTY);
			else if (stack.getItem() == ESInit.ITEMS.PARCHMENT) list.set(i, ItemStack.EMPTY);
			else {
				ItemStack in = stack.copy();
				in.setCount(1);
				list.set(i, in);
			}
		}
		// inv.clear();
		return list;

	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ESInit.ITEMS.PARCHMENT);
	}

	@Override
	public NonNullList<Ingredient> getIngredients(ItemStack output) {
		ItemStack inner = getInnerStack(output);
		if (inner.isEmpty()) return this.getIngredients();
		TileRiteTable.Recipe r = TileRiteTable.findRecipe(inner);
		if (r == null) return this.getIngredients();
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(Ingredient.fromStacks(new ItemStack(Items.FEATHER)));
		list.add(Ingredient.fromStacks(new ItemStack(Items.DYE)));
		list.add(Ingredient.fromStacks(new ItemStack(ESInit.ITEMS.PARCHMENT)));
		list.add(Ingredient.fromStacks(r.parchmentInput()));
		return list;
	}

}
