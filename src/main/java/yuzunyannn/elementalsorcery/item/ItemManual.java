package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.Pages;

public class ItemManual extends Item {

	@Config(sync = true)
	private int MANUAL_MAX_PAGES = 32;

	public ItemManual() {
		this.setTranslationKey("manual");
		this.setMaxStackSize(1);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this));
			ItemStack stack = new ItemStack(this);
			getNBT(stack).setBoolean("all", true);
			items.add(stack);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (handIn != EnumHand.MAIN_HAND) return super.onItemRightClick(worldIn, playerIn, handIn);
		if (playerIn.isSneaking()) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			this.store(playerIn, stack);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		// 没有shift
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!Pages.getPage(stack).getId().equals(Pages.BOOK)) Pages.setPage(Pages.BOOK, stack);
		if (worldIn.isRemote) {
			NBTTagCompound nbt = getNBT(stack);
			if (nbt.getBoolean("all")) Pages.getBookPage().setIds(Pages.getPageNormalIds());
			else Pages.getBookPage().setIds(ItemManual.getIds(stack));
			BlockPos pos = playerIn.getPosition();
			playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_PARCHMENT, worldIn, pos.getX(), pos.getY(),
					pos.getZ());
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.YELLOW + I18n.format("info.manual.info"));
		NBTTagCompound nbt = getNBT(stack);
		if (nbt.getBoolean("all")) {
			tooltip.add(TextFormatting.GRAY + I18n.format("info.manual.count", Pages.getCount(), Pages.getCount()));
		} else {
			NBTTagList nbtList = ItemManual.getIds(stack);
			tooltip.add(TextFormatting.GRAY + I18n.format("info.manual.count", nbtList.tagCount(), MANUAL_MAX_PAGES));
		}
	}

	// 获取储存数据
	private static NBTTagCompound getNBT(ItemStack stack) {
		return stack.getOrCreateSubCompound("manual");
	}

	// 获取ids列表
	public static NBTTagList getIds(ItemStack stack) {
		NBTTagCompound nbt = getNBT(stack);
		return nbt.getTagList("ids", 8);
	}

	// 设置ids列表
	public static ItemStack setIds(ItemStack stack, NBTTagList ids) {
		NBTTagCompound nbt = getNBT(stack);
		nbt.setTag("ids", ids);
		return stack;
	}

	// 存入
	private void store(EntityPlayer player, ItemStack stack) {
		if (!player.isServerWorld()) return;
		if (getNBT(stack).getBoolean("all")) return;
		NBTTagList idsList = ItemManual.getIds(stack);
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = player.inventory.getStackInSlot(i);
			if (itemstack.getItem() != ESObjects.ITEMS.PARCHMENT) continue;
			Page page = Pages.getPage(itemstack);
			if (Pages.ERROR.equals(page.getId())) continue;
			boolean has = false;
			for (NBTBase base : idsList) {
				NBTTagString nbtstr = (NBTTagString) base;
				String str = nbtstr.getString();
				if (str.equals(page.getId())) {
					has = true;
					break;
				}
			}
			if (has) player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
			else {
				if (idsList.tagCount() < MANUAL_MAX_PAGES) {
					idsList.appendTag(new NBTTagString(page.getId()));
					player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
				}
			}
		}
		ItemManual.setIds(stack, idsList);
	}

}
