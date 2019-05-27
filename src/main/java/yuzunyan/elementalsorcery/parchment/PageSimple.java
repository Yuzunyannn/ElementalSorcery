package yuzunyan.elementalsorcery.parchment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PageSimple extends Page {

	private final String name;
	private final ItemStack icon;

	public PageSimple(String name) {
		this.name = name;
		this.icon = ItemStack.EMPTY;
	}

	public PageSimple(String name, ItemStack icon) {
		this.name = name;
		this.icon = icon;
	}
	
	public PageSimple(String name, Item icon) {
		this(name,new ItemStack(icon));
	}


	@Override
	public String getTitle() {
		return "page." + name;
	}

	@Override
	public String getContext() {
		return "page." + name + ".ct";
	}

	@Override
	public ItemStack getIcon() {
		return icon;
	}
}
