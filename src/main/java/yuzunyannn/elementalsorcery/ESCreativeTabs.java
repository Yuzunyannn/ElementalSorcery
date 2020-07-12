package yuzunyannn.elementalsorcery;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class ESCreativeTabs extends CreativeTabs {

	public static final ESCreativeTabs TAB = new ESCreativeTabs();

	public ESCreativeTabs() {
		super("ElementalSorcery");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(ESInitInstance.ITEMS.SPELLBOOK);
	}
}
