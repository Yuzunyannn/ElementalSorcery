package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemWindmillBladeFrame extends Item {

	public ItemWindmillBladeFrame() {
		this.setUnlocalizedName("windmillBladeFrame");
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.getMetadata() == 1) return super.getUnlocalizedName(stack) + ".adv";
		return super.getUnlocalizedName(stack);
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
