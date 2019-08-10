package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.item.ItemStack;

public class PageCraftingTemp extends PageCrafting {
	public PageCraftingTemp(ItemStack stack) {
		super(stack);
	}

	@Override
	public void drawValue(IPageManager pageManager) {
	}

	@Override
	protected int getCX() {
		return 101;
	}

	@Override
	protected int getCY() {
		return 55;
	}
}
