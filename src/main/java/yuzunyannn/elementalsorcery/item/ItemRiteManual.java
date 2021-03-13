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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.container.gui.GuiRiteManual;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.ColorHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class ItemRiteManual extends Item {

	@Config(kind = "item")
	@Config.NumberRange(max = 128, min = 0)
	private static int MAX_RECORD = 10;

	public ItemRiteManual() {
		this.setUnlocalizedName("riteManual");
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		BlockPos pos = playerIn.getPosition();
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_RITE_MANUAL, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
		NBTTagList list = nbt.getTagList("recs", NBTTag.TAG_COMPOUND);
		if (list.hasNoTags()) return;
		tooltip.add(TextFormatting.GOLD + I18n.format("info.following.record"));
		int i = 0;
		for (NBTBase base : list) {
			if (i > MAX_RECORD) {
				tooltip.add(TextFormatting.YELLOW + "...");
				break;
			}
			i++;
			NBTTagCompound data = (NBTTagCompound) base;
			ItemStack origin = new ItemStack(data.getCompoundTag("item"));
			tooltip.add(TextFormatting.YELLOW + origin.getDisplayName());
		}
	}

	public static void addRecord(ItemStack manual, ItemStack recStack, int level, int power) {
		if (manual.isEmpty()) return;
		if (MAX_RECORD <= 0) return;
		NBTTagCompound nbt = manual.getTagCompound();
		if (nbt == null) manual.setTagCompound(nbt = new NBTTagCompound());
		NBTTagList list = nbt.getTagList("recs", NBTTag.TAG_COMPOUND);
		NBTTagList newList = new NBTTagList();
		for (int i = Math.max(list.tagCount() - MAX_RECORD + 1, 0); i < list.tagCount(); i++) {
			NBTTagCompound data = list.getCompoundTagAt(i);
			ItemStack origin = new ItemStack(data.getCompoundTag("item"));
			if (origin.isItemEqual(recStack)) return; // 找到了，就不记录了
			newList.appendTag(data);
		}

		{
			NBTTagCompound data = new NBTTagCompound();
			data.setTag("item", recStack.serializeNBT());
			data.setInteger("power", power);
			data.setInteger("level", level);
			newList.appendTag(data);
		}

		nbt.setTag("recs", newList);
	}

	static public ItemStack findRiteManual(EntityPlayer player) {
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		if (stack.getItem() == ESInit.ITEMS.RITE_MANUAL) return stack;
		stack = player.getHeldItem(EnumHand.OFF_HAND);
		if (stack.getItem() == ESInit.ITEMS.RITE_MANUAL) return stack;
		return ItemStack.EMPTY;
	}

	@SideOnly(Side.CLIENT)
	static public void drawTooltip(ItemTooltipEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player == null) return;
		ItemStack riteManual = findRiteManual(player);
		if (riteManual.isEmpty()) return;
		NBTTagCompound nbt = riteManual.getTagCompound();
		if (nbt == null) return;
		NBTTagList list = nbt.getTagList("recs", NBTTag.TAG_COMPOUND);
		NBTTagCompound get = null;
		for (NBTBase base : list) {
			NBTTagCompound data = (NBTTagCompound) base;
			ItemStack origin = new ItemStack(data.getCompoundTag("item"));
			if (origin.isItemEqual(event.getItemStack())) {
				get = data;
				break;
			}
		}
		if (get == null) return;
		List<String> toolTip = event.getToolTip();
		StringBuilder sb = new StringBuilder();
		sb.append(TextFormatting.GOLD);
		sb.append(I18n.format("item.riteManual.name"));
		sb.append(": ");
		GuiRiteManual.Level[] levels = GuiRiteManual.toLevels(get.getInteger("level"), get.getInteger("power"));
		for (int i = 0; i < levels.length; i++) {
			GuiRiteManual.Level lev = levels[i];
			sb.append(TextFormatting.GRAY);
			sb.append(i).append('-');
			sb.append(ColorHelper.toTextFormatting(lev.getDyeColor()));
			sb.append(I18n.format("lev." + lev.name().toLowerCase()));
			sb.append(' ');
		}
		toolTip.add(sb.toString());
	}
}
