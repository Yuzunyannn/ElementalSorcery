package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.item.ItemStack;

public class PageCraftingSimple extends PageCrafting {

	private final String title;
	private final String value;

	public PageCraftingSimple(String title, String value, ItemStack... stacks) {
		super(stacks);
		this.title = title;
		this.value = value;
	}

	@Override
	public String getTitle() {
		return "es.page." + title + ".title";
	}

	@Override
	public String getContext() {
		return "es.page." + value + ".describe";
	}
}
