package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.item.ItemStack;

public class PageSmeltingSimple extends PageSmelting {
	protected final String title;
	protected final String value;

	public PageSmeltingSimple(String title, String value, ItemStack stack) {
		super(stack);
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
