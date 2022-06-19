package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemElfPurse extends Item {

	public ItemElfPurse() {
		this.setTranslationKey("elfPurse");
		this.setMaxStackSize(1);
	}

	protected int getAndClearAllCoin(EntityPlayer player) {
		IInventory inv = player.inventory;
		int n = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() != ESInit.ITEMS.ELF_COIN) continue;
			n += stack.getCount();
			inv.setInventorySlotContents(i, ItemStack.EMPTY);
		}
		return n;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		int coin = nbt.getInteger("coin");
		nbt.setInteger("coin", coin + this.getAndClearAllCoin(playerIn));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		int coin = 0;
		if (nbt != null) coin = nbt.getInteger("coin");
		tooltip.add(I18n.format("info.elfPurse.coin", coin));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			ItemStack stack = new ItemStack(this);
			items.add(stack);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("coin", 1000000);
		}
	}

	static public ItemStack getPurse(int coin) {
		ItemStack stack = new ItemStack(ESInit.ITEMS.ELF_PURSE);
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger("coin", coin);
		return stack;
	}

	static public int getCoinFromPurse(ItemStack purse) {
		NBTTagCompound tag = purse.getTagCompound();
		return tag == null ? 0 : tag.getInteger("coin");
	}

	/**
	 * 提取硬币
	 * 
	 * @return 还差多少没提取
	 */
	static public int extract(IInventory inv, int need, boolean simulate) {
		for (int i = 0; i < inv.getSizeInventory() && need > 0; i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == ESInit.ITEMS.ELF_COIN) {
				int ex = Math.min(need, stack.getCount());
				need -= ex;
				if (!simulate) stack.splitStack(ex);
			} else if (stack.getItem() == ESInit.ITEMS.ELF_PURSE) {
				NBTTagCompound nbt = stack.getTagCompound();
				if (nbt == null) continue;
				int coin = nbt.getInteger("coin");
				int ex = Math.min(need, coin);
				need -= ex;
				if (!simulate) {
					coin -= ex;
					nbt.setInteger("coin", coin);
				}
			}
		}
		return need;
	}

	/**
	 * 插入硬币
	 * 
	 * @return 没插入的值
	 */
	static public int insert(IInventory inv, int count, boolean simulate) {
		ItemStack purse = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == ESInit.ITEMS.ELF_PURSE) {
				purse = stack;
				break;
			}
		}
		// 有钱袋的情况
		if (!purse.isEmpty()) {
			if (!simulate) {
				NBTTagCompound nbt = purse.getTagCompound();
				if (nbt == null) purse.setTagCompound(nbt = new NBTTagCompound());
				int coin = nbt.getInteger("coin");
				nbt.setInteger("coin", coin + count);
			}
			return 0;
		}
		// 没钱袋的情况
		for (int i = 0; i < inv.getSizeInventory() && count > 0; i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty() || stack.getItem() == ESInit.ITEMS.ELF_COIN) {
				int maxSize = ESInit.ITEMS.ELF_COIN.getItemStackLimit(stack);
				maxSize -= stack.getCount();
				int in = Math.min(maxSize, count);
				count -= in;
				if (!simulate) {
					if (stack.isEmpty()) inv.setInventorySlotContents(i, new ItemStack(ESInit.ITEMS.ELF_COIN, in));
					else stack.grow(in);
				}
			}
		}
		return count;

	}

	/** 给予玩家硬币，与插入仓库不同，如果玩家仓库满了，会扔出去 */
	static public void insert(EntityPlayer player, int count) {
		IInventory inv = player.inventory;
		ItemStack purse = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == ESInit.ITEMS.ELF_PURSE) {
				purse = stack;
				break;
			}
		}
		// 有钱袋的情况
		if (!purse.isEmpty()) {
			NBTTagCompound nbt = purse.getTagCompound();
			if (nbt == null) purse.setTagCompound(nbt = new NBTTagCompound());
			int coin = nbt.getInteger("coin");
			nbt.setInteger("coin", coin + count);
			return;
		}
		ItemHelper.addItemStackToPlayer(player, new ItemStack(ESInit.ITEMS.ELF_COIN, count));
	}
}
