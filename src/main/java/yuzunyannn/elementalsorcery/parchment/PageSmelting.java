package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class PageSmelting extends Page {

	private ItemStack smelting = ItemStack.EMPTY;
	private ItemStack ouput = ItemStack.EMPTY;
	private ItemStack extra = ItemStack.EMPTY;

	public PageSmelting(ItemStack stack) {
		this.addSmelting(stack);
	}

	public PageSmelting(Block block) {
		this(new ItemStack(block));
	}

	public PageSmelting(Item item) {
		this(new ItemStack(item));
	}

	public void addSmelting(ItemStack stack) {
		ItemStack new_stack = FurnaceRecipes.instance().getSmeltingResult(stack);
		if (new_stack.isEmpty())
			return;
		ouput = new_stack;
		smelting = stack;
	}

	@Override
	public ItemStack getOrigin() {
		return smelting;
	}

	@Override
	public ItemStack getOutput() {
		return ouput;
	}

	@Override
	public ItemStack getExtra() {
		return extra;
	}
}
