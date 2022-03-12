package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemWindmillBladeFrame extends Item {

	public ItemWindmillBladeFrame() {
		this.setTranslationKey("windmillBladeFrame");
		this.setHasSubtypes(true);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (stack.getMetadata() == 1) return super.getTranslationKey(stack) + ".adv";
		return super.getTranslationKey(stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 1));
		}
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

}
