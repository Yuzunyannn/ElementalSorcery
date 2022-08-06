package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public class ElfMerchantTypeTool extends ElfMerchantTypeEquipment {

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(Items.DIAMOND_PICKAXE);
	}

	@Override
	public boolean isTargetEuipment(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemTool) return true;
		if (item instanceof ItemHoe) return true;
		if (item.getCreativeTab() == CreativeTabs.TOOLS) return true;
		Set<String> classs = item.getToolClasses(stack);
		return classs != null && !classs.isEmpty();
	}

}
