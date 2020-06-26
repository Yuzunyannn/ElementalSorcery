package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PageCraftingSimple extends PageCrafting {

	private final String title;
	private final String value;

	public PageCraftingSimple(String name, ItemStack... stacks) {
		super(stacks);
		this.title = "page." + name;
		this.value = title + ".ct";
	}

	public PageCraftingSimple(String name, Block block) {
		super(block);
		this.title = "page." + name;
		this.value = title + ".ct";
	}

	public PageCraftingSimple(String name, Item item) {
		super(item);
		this.title = "page." + name;
		this.value = title + ".ct";
	}

	public PageCraftingSimple(String title, String value, ItemStack... stacks) {
		super(stacks);
		this.title = title;
		this.value = value;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getContext() {
		return value;
	}
}
