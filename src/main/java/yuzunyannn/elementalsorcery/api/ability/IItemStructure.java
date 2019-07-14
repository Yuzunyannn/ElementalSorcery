package yuzunyannn.elementalsorcery.api.ability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.IToElement;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;

public interface IItemStructure extends IToElement, IItemCapbiltitySyn {

	@Override
	default ElementStack[] toElement(Item item) {
		return this.toElement(new ItemStack(item));
	}

	@Override
	default int complex(Item item) {
		return this.complex(new ItemStack(item));
	}

	int getItemCount();

	ElementStack[] toElement(int index);

	int complex(int index);

	ItemStack getStructureItem(int index);

	public static interface IItemStructureSet {
		public ItemStructure set(ItemStack stack, int complex, ElementStack... estacks);
	}
}
