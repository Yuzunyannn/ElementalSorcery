package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PageCraftingSimple extends PageCrafting {

	private final String name;

	public PageCraftingSimple(String name, ItemStack... stacks) {
		super(stacks);
		this.name = name;
	}

	public PageCraftingSimple(String name, Block block) {
		super(block);
		this.name = name;
	}

	public PageCraftingSimple(String name, Item item) {
		super(item);
		this.name = name;
	}

	@Override
	public String getTitle() {
		return "page." + name;
	}

	@Override
	public String getContext() {
		return "page." + name + ".ct";
	}

}
