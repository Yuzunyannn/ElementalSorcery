package yuzunyannn.elementalsorcery.crafting.mc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.parchment.Pages;

public class RecipePageBuilding extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe>
		implements IRecipe {

	public RecipePageBuilding() {

	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		int parchment = -1;
		int archCrystal = -1;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == ESInit.ITEMS.PARCHMENT) {
				if (parchment == -1) parchment = i;
				else return false;
			} else if (stack.getItem() == ESInit.ITEMS.ARCHITECTURE_CRYSTAL) {
				if (archCrystal == -1) archCrystal = i;
				else return false;
			} else return false;
		}
		if (parchment == -1 || archCrystal == -1) return false;
		Building building = Pages.getBuildingInPage(inv.getStackInSlot(parchment));
		if (building == null) return false;
		ArcInfo info = new ArcInfo(inv.getStackInSlot(archCrystal), worldIn.isRemote ? Side.CLIENT : Side.SERVER);
		return !info.isValid();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack parchment = ItemStack.EMPTY;
		ItemStack archCrystal = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == ESInit.ITEMS.PARCHMENT) parchment = stack;
			else if (stack.getItem() == ESInit.ITEMS.ARCHITECTURE_CRYSTAL) archCrystal = stack;
		}
		archCrystal = archCrystal.copy();
		archCrystal.setCount(1);
		Building building = Pages.getBuildingInPage(parchment);
		ArcInfo.initArcInfoToItem(archCrystal, building.getKeyName());
		return archCrystal;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) list.set(i, ItemStack.EMPTY);
			else if (stack.getItem() == ESInit.ITEMS.PARCHMENT) {
				stack = stack.copy();
				stack.setCount(1);
				list.set(i, stack);
			} else list.set(i, ItemStack.EMPTY);
		}
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

}
