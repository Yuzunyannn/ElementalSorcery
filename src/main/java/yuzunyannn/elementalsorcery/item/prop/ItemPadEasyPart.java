package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class ItemPadEasyPart extends Item {

	public static ItemStack create(ItemPadEasyPart.EnumType type, int n) {
		return new ItemStack(ESObjects.ITEMS.PAD_EASY_PART, n, type.getMeta());
	}

	public ItemPadEasyPart() {
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		for (EnumType type : EnumType.values()) {
			if (!type.join) continue;
			items.add(new ItemStack(this, 1, type.getMeta()));
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		EnumType type = EnumType.fromId(stack.getMetadata());
		if (!type.join) return "item.padEasy." + "unknow";
		return "item." + type.getTranslationKey();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	public static enum EnumType {
		FLUORESCENT_PARTICLE("fluorescentParticle", true),
		CONTROL_CIRCUIT("controlCircuit", true),
		ACCESS_CIRCUIT("accessCircuit", false),
		DISPLAY_CIRCUIT("displayCircuit", false),
		CALCULATE_CIRCUIT("calculateCircuit", false),
		WIFI_CIRCUIT("wifiCircuit", false);

		final String unlocalizedName;
		final public boolean join;

		EnumType(String unlocalizedName, boolean isJoin) {
			this.unlocalizedName = unlocalizedName;
			this.join = isJoin;
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
