package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class PageError extends PageEasy {

	static public ItemStack ICON = new ItemStack(Blocks.BARRIER);
	
	@Override
	public String getTitle() {
		return "es.pageui.error";
	}

	@Override
	public String getContext() {
		return "es.pageui.error.ct";
	}

	@Override
	public ItemStack getIcon() {
		return ICON;
	}
}
