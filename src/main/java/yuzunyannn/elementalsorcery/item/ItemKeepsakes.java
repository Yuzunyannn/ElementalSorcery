package yuzunyannn.elementalsorcery.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ItemKeepsakes extends Item {

	public static ItemStack create(ItemKeepsakes.EnumType type, int n) {
		return new ItemStack(ESInit.ITEMS.KEEPSAKES, n, type.getMeta());
	}

	public ItemKeepsakes() {
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		for (EnumType type : EnumType.values()) items.add(new ItemStack(this, 1, type.getMeta()));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item." + EnumType.fromId(stack.getMetadata()).getUnlocalizedName();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	public static enum EnumType {
		RELIC_FRAGMENT("relicFragment");

		final String unlocalizedName;

		EnumType(String unlocalizedName) {
			this.unlocalizedName = unlocalizedName;
		}

		public int getMeta() {
			return this.ordinal();
		}

		public String getUnlocalizedName() {
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
