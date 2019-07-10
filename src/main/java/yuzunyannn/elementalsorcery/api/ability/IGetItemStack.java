package yuzunyannn.elementalsorcery.api.ability;

import net.minecraft.item.ItemStack;

public interface IGetItemStack {

	public void setStack(ItemStack stack);

	public ItemStack getStack();

	default public boolean canSetStack(ItemStack stack) {
		return true;
	}
}
