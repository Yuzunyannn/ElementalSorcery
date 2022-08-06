package yuzunyannn.elementalsorcery.elf.pro.merchant;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public class ElfMerchantTypeWeapon extends ElfMerchantTypeEquipment {

	@Override
	public ItemStack getHoldItem(World world, VariableSet storage) {
		return new ItemStack(Items.DIAMOND_SWORD);
	}

	@Override
	public boolean isTargetEuipment(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemSword) return true;
		if (item instanceof ItemBow) return true;
		if (item instanceof ItemTool) return item.getToolClasses(stack).contains("axe");
		if (item instanceof ItemHoe) return false;
		Multimap<String, AttributeModifier> map = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
		return hasKey(map, SharedMonsterAttributes.ATTACK_DAMAGE);
	}

}
