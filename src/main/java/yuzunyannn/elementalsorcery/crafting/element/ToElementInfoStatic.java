package yuzunyannn.elementalsorcery.crafting.element;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.element.ElementStack;

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

}
