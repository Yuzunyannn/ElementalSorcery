package yuzunyannn.elementalsorcery.crafting.mc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.TileLifeDirt;

public class RecipeLifeDirt extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	protected boolean canCraft(ItemStack stack) {
		Item item = stack.getItem();
		Block block = Block.getBlockFromItem(item);
		if (item instanceof IPlantable) return true;
		if (block == ESInitInstance.BLOCKS.CRYSTAL_FLOWER) return false;
		if (block instanceof BlockSapling) return false;
		if (stack.getItem() == Items.REEDS || block == Blocks.CACTUS) return true;
		if (block instanceof BlockBush) return true;
		// if (item instanceof ItemCrystal) return true;
		return block instanceof IGrowable;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ItemStack lifeDirt = ItemStack.EMPTY;
		ItemStack other = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (Block.getBlockFromItem(stack.getItem()) == ESInitInstance.BLOCKS.LIFE_DIRT) {
				if (lifeDirt.isEmpty()) lifeDirt = stack;
				else return false;
			} else if (this.canCraft(stack)) {
				if (other.isEmpty()) other = stack;
				else return false;
			} else return false;
		}
		return !lifeDirt.isEmpty() && !other.isEmpty() && TileLifeDirt.getPlant(lifeDirt).isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack lifeDirt = ItemStack.EMPTY;
		ItemStack other = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (Block.getBlockFromItem(stack.getItem()) == ESInitInstance.BLOCKS.LIFE_DIRT) lifeDirt = stack;
			else other = stack;
		}
		lifeDirt = lifeDirt.copy();
		lifeDirt.setCount(1);
		TileLifeDirt.setPlant(lifeDirt, other);
		return lifeDirt;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 2 && height >= 2;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ESInitInstance.BLOCKS.LIFE_DIRT);
	}

}
