package yuzunyan.elementalsorcery.parchment;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PageSmeltingSimple extends PageSmelting {

	private final String name;

	public PageSmeltingSimple(String name, ItemStack stack) {
		super(stack);
		this.name = name;
	}

	public PageSmeltingSimple(String name, Block block) {
		super(block);
		this.name = name;
	}

	public PageSmeltingSimple(String name, Item item) {
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
