package yuzunyan.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.init.ESInitInstance;

public class ItemScroll extends Item {

	public ItemScroll() {
		this.setUnlocalizedName("scroll");
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		NBTTagCompound nbt = stack.getOrCreateSubCompound("scroll");
		int[] ids = nbt.getIntArray("parIds");
		if (ids.length == 0) {
			for (int i = 0; i < 3; i++)
				playerIn.inventory.addItemStackToInventory(ItemParchment.getParchment(0));
		} else
			for (int id : ids)
				playerIn.inventory.addItemStackToInventory(ItemParchment.getParchment(id));
		ItemStack newStack = stack.copy();
		newStack.shrink(1);
		playerIn.setHeldItem(handIn, newStack);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int[] ids = null;
		NBTTagCompound nbt = stack.getSubCompound("scroll");
		if (nbt != null) {
			if (nbt.hasKey("parIds"))
				ids = nbt.getIntArray("parIds");
		}
		if (ids == null) {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.scroll.none", 3));
		} else {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.scroll.has", ids.length));
		}
	}

	/** 设置卷轴的ids */
	static public ItemStack setIds(ItemStack stack, int[] ids) {
		stack.getOrCreateSubCompound("scroll").setIntArray("parIds", ids);
		return stack;
	}

	/** 获取卷轴 */
	static public ItemStack getScroll(int... ids) {
		ItemStack stack = new ItemStack(ESInitInstance.ITEMS.SCROLL);
		return ItemScroll.setIds(stack, ids);
	}
}
