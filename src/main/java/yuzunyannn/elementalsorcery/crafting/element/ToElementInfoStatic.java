package yuzunyannn.elementalsorcery.crafting.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class ToElementInfoStatic {

	public static IToElementInfo create(int complex, ElementStack... estacks) {
		return new IToElementInfo() {
			@Override
			public int complex() {
				return complex;
			}

			@Override
			public ElementStack[] element() {
				return estacks;
			}
		};
	}

	public static IToElementInfo create(int complex, ItemStack remain, ElementStack... estacks) {
		return create(complex, new ItemStack[] { remain }, estacks);
	}

	public static IToElementInfo create(int complex, ItemStack[] remain, ElementStack... estacks) {
		return new IToElementInfo() {
			@Override
			public int complex() {
				return complex;
			}

			@Override
			public ElementStack[] element() {
				return estacks;
			}

			@Override
			public ItemStack[] remain() {
				return remain;
			}
		};
	}

	public static IToElementInfo create(int complex, ItemStack remain, Collection<ElementStack> estacks) {
		return create(complex, new ItemStack[] { remain }, estacks.toArray(new ElementStack[estacks.size()]));
	}

	public static IToElementInfo createWithElementContainer(ItemStack elementContainer,
			@Nullable IElementInventory eInv) {

		eInv = eInv == null ? ElementHelper.getElementInventory(elementContainer) : eInv;
		if (eInv == null) return null;
		eInv.applyUse();

		List<ElementStack> elements = new ArrayList<>(eInv.getSlots());
		for (int i = 0; i < eInv.getSlots(); i++) {
			ElementStack eStack = eInv.getStackInSlot(i);
			if (eStack.isEmpty()) continue;
			elements.add(eStack);
			eInv.setStackInSlot(i, ElementStack.EMPTY);
		}

		if (elements.isEmpty()) return null;
		eInv.markDirty();

		return ToElementInfoStatic.create(1, elementContainer, elements);
	}

}
