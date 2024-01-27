package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.IItemHandlerModifiable;

/** ItemStackHandlerVest到Inventory的马甲 */
public class InventoryVest implements IItemStackHandlerInventory {

	protected IItemHandlerModifiable handler;

	public InventoryVest(IItemHandlerModifiable itemHandler) {
		handler = itemHandler;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public IItemHandlerModifiable getItemStackHandler() {
		return handler;
	}

}
