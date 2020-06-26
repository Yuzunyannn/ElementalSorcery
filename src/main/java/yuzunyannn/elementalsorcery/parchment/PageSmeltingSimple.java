package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PageSmeltingSimple extends PageSmelting {
	protected final String title;
	protected final String value;

	public PageSmeltingSimple(String name, ItemStack stack) {
		super(stack);
		this.title = "page." + name;
		this.value = title + ".ct";
	}

	public PageSmeltingSimple(String name, Block block) {
		super(block);
		this.title = "page." + name;
		this.value = title + ".ct";
	}

	public PageSmeltingSimple(String name, Item item) {
		super(item);
		this.title = "page." + name;
		this.value = title + ".ct";
	}

	public PageSmeltingSimple(String title, String value, ItemStack stack) {
		super(stack);
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
