package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ItemSoulWoodSword extends ItemSword {

	public ItemSoulWoodSword() {
		super(ToolMaterial.WOOD);
		this.setUnlocalizedName("soulWoodSword");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		items.add(new ItemStack(this));
		ItemStack soulWith666 = new ItemStack(this);
		ItemSoulWoodSword.addSoul(soulWith666, 666);
		items.add(soulWith666);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.AQUA + I18n.format("info.soul.power", getSoul(stack)));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
		// 收集所有物品栏内的灵魂碎片
		ItemStack stack = player.getHeldItem(hand);
		if (ItemSoulWoodSword.collectSouls(stack, player))
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		return super.onItemRightClick(worldIn, player, hand);
	}

	public static boolean collectSouls(ItemStack stack, EntityPlayer player) {
		boolean success = false;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack item = player.inventory.getStackInSlot(i);
			if (item.isEmpty()) continue;
			if (item.getItem() == ESInit.ITEMS.SOUL_FRAGMENT) {
				int count = item.getCount();
				item.shrink(count);
				addSoul(stack, count);
				success = true;
			}
		}
		return success;
	}

	public static void addSoul(ItemStack stack, int count) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		nbt.setInteger("soul", nbt.getInteger("soul") + count);
	}

	public static int getSoul(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return 0;
		return nbt.getInteger("soul");
	}

}
