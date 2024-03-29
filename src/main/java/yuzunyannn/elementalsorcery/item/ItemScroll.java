package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemScroll extends Item {

	public ItemScroll() {
		this.setTranslationKey("scroll");
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		NBTTagCompound nbt = stack.getOrCreateSubCompound("scroll");
		NBTTagList ids = nbt.getTagList("parIds", 8);
		if (ids.tagCount() == 0) {
			for (int i = 0; i < 3; i++) ItemHelper.addItemStackToPlayer(playerIn, ItemParchment.getParchment(null));
		} else for (NBTBase base : ids) {
			NBTTagString bntStr = (NBTTagString) base;
			ItemHelper.addItemStackToPlayer(playerIn, ItemParchment.getParchment(bntStr.getString()));
		}
		ItemStack newStack = stack.copy();
		newStack.shrink(1);
		playerIn.setHeldItem(handIn, newStack);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagList ids = null;
		NBTTagCompound nbt = stack.getSubCompound("scroll");
		if (nbt != null) {
			if (nbt.hasKey("parIds")) ids = nbt.getTagList("parIds", 8);
		}
		if (ids == null) {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.scroll.none", 3));
		} else {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.scroll.has", ids.tagCount()));
		}
	}

	/** 设置卷轴的ids */
	static public ItemStack setIds(ItemStack stack, String[] ids) {
		stack.getOrCreateSubCompound("scroll").setTag("parIds", NBTHelper.stringToNBTTagList(ids));
		return stack;
	}

	/** 获取卷轴 */
	static public ItemStack getScroll(String... ids) {
		ItemStack stack = new ItemStack(ESObjects.ITEMS.SCROLL);
		return ItemScroll.setIds(stack, ids);
	}
}
