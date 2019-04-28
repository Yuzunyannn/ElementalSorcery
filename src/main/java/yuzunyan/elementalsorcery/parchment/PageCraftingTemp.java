package yuzunyan.elementalsorcery.parchment;

import net.minecraft.item.ItemStack;

public class PageCraftingTemp extends PageCrafting {

	String un_name;

	public PageCraftingTemp(ItemStack stack) {
		super(stack);
		un_name = stack.getUnlocalizedName();
	}

	@Override
	public String getTitle() {
		return un_name + ".name";
	}

	@Override
	public PageSate getState() {
		return PageSate.EXCLUSIVE;
	}
}
