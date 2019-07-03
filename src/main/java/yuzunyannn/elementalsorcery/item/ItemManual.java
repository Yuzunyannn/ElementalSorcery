package yuzunyannn.elementalsorcery.item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.parchment.Pages;

public class ItemManual extends Item {

	public ItemManual() {
		this.setUnlocalizedName("manual");
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (handIn != EnumHand.MAIN_HAND)
			return super.onItemRightClick(worldIn, playerIn, handIn);
		if (playerIn.isSneaking()) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			this.store(playerIn, stack);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		// 没有shift
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (Pages.getPageId(stack) != Pages.BOOK)
			Pages.setPageAt(stack, Pages.BOOK);
		if (worldIn.isRemote) {
			Pages.getBookPage().setPageIds(ItemManual.getIds(stack));
			BlockPos pos = playerIn.getPosition();
			playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_PARCHMENT, worldIn, pos.getX(), pos.getY(),
					pos.getZ());
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.YELLOW + I18n.format("info.manual.info"));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab))
			return;
		int[] ids = new int[Pages.getMax() - 2];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = i + 2;
		}
		items.add(ItemManual.setIds(new ItemStack(this), ids));
		ItemStack stack;
	}

	// 获取ids列表
	public static int[] getIds(ItemStack stack) {
		NBTTagCompound nbt = stack.getOrCreateSubCompound("manual");
		return nbt.getIntArray("ids");
	}

	// 设置ids列表
	public static ItemStack setIds(ItemStack stack, int... ids) {
		NBTTagCompound nbt = stack.getOrCreateSubCompound("manual");
		nbt.setIntArray("ids", ids);
		return stack;
	}

	// 存入
	private void store(EntityPlayer player, ItemStack stack) {
		int[] ids = ItemManual.getIds(stack);
		List<Integer> idsList = Arrays.stream(ids).boxed().collect(Collectors.toList());
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = player.inventory.getStackInSlot(i);
			if (itemstack.getItem() != ESInitInstance.ITEMS.PARCHMENT)
				continue;
			int id = Pages.getPageId(itemstack);
			if (id != 0) {
				if (!idsList.contains(id)) {
					idsList.add(id);
				}
			}
			player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
		}
		ids = idsList.stream().mapToInt(Integer::valueOf).toArray();
		ItemManual.setIds(stack, ids);
	}

}
