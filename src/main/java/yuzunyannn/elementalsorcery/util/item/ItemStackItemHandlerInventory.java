package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ItemStackItemHandlerInventory extends ItemStackHandlerInventory {

	protected final ItemStack stack;
	protected final String tagName;

	public ItemStackItemHandlerInventory(ItemStack stack, int size, String tagName) {
		super(size);
		this.stack = stack;
		this.tagName = tagName;
		this.deserializeNBT(this.stack.getOrCreateSubCompound(tagName));
	}

	@Override
	protected void onContentsChanged(int slot) {
		NBTHelper.getStackTag(stack).setTag(tagName, this.serializeNBT());
	}
}
