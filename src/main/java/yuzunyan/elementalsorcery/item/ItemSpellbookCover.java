package yuzunyan.elementalsorcery.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemSpellbookCover extends Item {

	public ItemSpellbookCover() {
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab))
			return;
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.getMetadata() == 0)
			return "item.spellbookCover";
		return "item.spellbookCover.back";
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

}
