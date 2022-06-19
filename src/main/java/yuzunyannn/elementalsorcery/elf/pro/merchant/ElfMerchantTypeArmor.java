package yuzunyannn.elementalsorcery.elf.pro.merchant;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.var.VariableSet;

public class ElfMerchantTypeArmor extends ElfMerchantTypeEquipment {

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(Items.DIAMOND_CHESTPLATE);
	}

	@Override
	public boolean isTargetEuipment(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemArmor) return true;
		Multimap<String, AttributeModifier> map = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
		return hasKey(map, SharedMonsterAttributes.ARMOR);
	}

}
