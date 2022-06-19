package yuzunyannn.elementalsorcery.crafting.element;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.util.ESEvent;

@Cancelable
public class EventGetItemStructure extends ESEvent {

	protected ItemStack itemStack;
	protected IItemStructure itemStructure;

	public EventGetItemStructure(ItemStack stack) {
		this.itemStack = stack;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public void setItemStructure(IItemStructure itemStructure) {
		this.itemStructure = itemStructure;
	}

	public IItemStructure getItemStructure() {
		return itemStructure;
	}

}
