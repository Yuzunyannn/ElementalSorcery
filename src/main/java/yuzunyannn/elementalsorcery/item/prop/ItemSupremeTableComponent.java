package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemSupremeTableComponent extends Item {

	public ItemSupremeTableComponent() {
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (stack.getMetadata() == 0) return "item.supremeTable.leg";
		return "item.supremeTable.top";
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

}
