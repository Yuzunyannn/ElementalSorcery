package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.item.ItemStack;

public class PageResearchSimple extends PageResearch {

	private final String title;
	private final String value;

	public PageResearchSimple(String title, String value, ItemStack output) {
		super(output);
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
