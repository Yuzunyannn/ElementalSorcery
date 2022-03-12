package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.item.tool.ItemSoulWoodSword;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class ItemLifeLeather extends Item {

	public ItemLifeLeather() {
		this.setTranslationKey("lifeLeather");
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
		if (stack.getMetadata() == 0) return super.getTranslationKey(stack) + ".incomplete";
		return super.getTranslationKey(stack);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.getMetadata() != 0) return;
		tooltip.add(TextFormatting.AQUA + I18n.format("info.soul.power", ItemSoulWoodSword.getSoul(stack)));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
		ItemStack stack = player.getHeldItem(handIn);
		if (stack.getMetadata() == 0) {
			if (ItemSoulWoodSword.collectSouls(stack, player)) {
				transform(stack);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		}

		// 完成
		if (!player.isCreative()) stack.shrink(1);

		ItemStack chest = new ItemStack(Items.LEATHER_CHESTPLATE);
		chest.addEnchantment(Enchantments.PROTECTION, 10);
		chest.addEnchantment(Enchantments.THORNS, Enchantments.THORNS.getMaxLevel());
		addAttributeModifier(chest, SharedMonsterAttributes.ARMOR, EntityEquipmentSlot.CHEST, 3, 0);
		addAttributeModifier(chest, SharedMonsterAttributes.MAX_HEALTH, EntityEquipmentSlot.CHEST, 0.5, 1);
		this.replace(player, EntityEquipmentSlot.CHEST, chest);

		ItemStack feet = new ItemStack(Items.LEATHER_BOOTS);
		feet.addEnchantment(Enchantments.FEATHER_FALLING, 10);
		feet.addEnchantment(Enchantments.DEPTH_STRIDER, Enchantments.DEPTH_STRIDER.getMaxLevel());
		this.replace(player, EntityEquipmentSlot.FEET, feet);

		ItemStack head = new ItemStack(Items.LEATHER_HELMET);
		head.addEnchantment(Enchantments.FIRE_PROTECTION, 10);
		head.addEnchantment(Enchantments.RESPIRATION, Enchantments.RESPIRATION.getMaxLevel());
		this.replace(player, EntityEquipmentSlot.HEAD, head);

		ItemStack legs = new ItemStack(Items.LEATHER_LEGGINGS);
		legs.addEnchantment(Enchantments.BLAST_PROTECTION, 10);
		addAttributeModifier(legs, SharedMonsterAttributes.ARMOR, EntityEquipmentSlot.LEGS, 2, 0);
		addAttributeModifier(legs, SharedMonsterAttributes.MOVEMENT_SPEED, EntityEquipmentSlot.LEGS, 0.5, 1);
		this.replace(player, EntityEquipmentSlot.LEGS, legs);

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	public static void addAttributeModifier(ItemStack stack, IAttribute attr, EntityEquipmentSlot slot, double amountIn,
			int operationIn) {
		stack.addAttributeModifier(attr.getName(), new AttributeModifier(attr.getName(), amountIn, operationIn), slot);
	}

	public void replace(EntityPlayer player, EntityEquipmentSlot slot, ItemStack item) {
		Items.LEATHER_CHESTPLATE.setColor(item, 0x3ae3f2);
		ItemStack stack = player.getItemStackFromSlot(slot);
		item.addEnchantment(Enchantments.MENDING, 10);
		item.addEnchantment(Enchantments.UNBREAKING, 10);
		item.addEnchantment(Enchantments.BINDING_CURSE, 1);
		item.addEnchantment(Enchantments.VANISHING_CURSE, 1);
		player.setItemStackToSlot(slot, item);
		if (stack.isEmpty()) return;
		player.dropItem(stack, false);
	}

	public static void transform(ItemStack leather) {
		int count = ItemSoulWoodSword.getSoul(leather);
		if (count >= 64 && RandomHelper.rand.nextFloat() * 256 < count) {
			leather.setItemDamage(1);
			leather.setTagCompound(null);
		}
	}

}
