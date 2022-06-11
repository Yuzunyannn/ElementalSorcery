package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ItemController extends Item {

	public static ItemStack create(ItemController.EnumType type, int n) {
		return new ItemStack(ESInit.ITEMS.CONTROLLER, n, type.getMeta());
	}

	public ItemController() {
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		for (EnumType type : EnumType.values()) items.add(new ItemStack(this, 1, type.getMeta()));
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return "item.controller." + EnumType.fromId(stack.getMetadata()).getTranslationKey();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	public static enum EnumType {
		ENERGY("energy"),
		INVERT("invert"),
		TIME("time"),
		CONCERT("concert"),
		SHELL("shell"),
		VOID("void");

		final String unlocalizedName;

		EnumType(String unlocalizedName) {
			this.unlocalizedName = unlocalizedName;
		}

		public int getMeta() {
			return this.ordinal();
		}

		public String getTranslationKey() {
			return unlocalizedName;
		}

		public String getName() {
			return this.name().toLowerCase();
		}

		public static EnumType fromId(int id) {
			EnumType[] types = EnumType.values();
			return types[id % types.length];
		}
	}
}
