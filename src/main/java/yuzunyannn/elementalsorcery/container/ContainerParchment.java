package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.Pages;

public class ContainerParchment extends Container {

	public final static int[] craftingRelative = new int[] { 0, 0, 18, 0, 36, 0, 0, 18, 18, 18, 36, 18, 0, 36, 18, 36,
			36, 36, -36, -36, -18, -36, 54, -36, 72, -36, -36, -18, -18, -18, 54, -18, 72, -18, -36, 54, -18, 54, 54,
			54, 72, 54, -36, 72, -18, 72, 54, 72, 72, 72 };

	EntityPlayer player;
	public Page page;

	public ContainerParchment(EntityPlayer player) {
		this.player = player;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		try {
			this.page = Pages.getPage(stack);
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new RuntimeException("这个界面请不要再服务器打开！");
		}
		this.page.back(-1);
		this.page.open();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		ItemStack stack = playerIn.getHeldItem(EnumHand.MAIN_HAND);
		return stack.getItem() == ESInitInstance.ITEMS.PARCHMENT;
	}

	public void changePage(int id, boolean isBack) {
		if (id <= 0)
			return;
		int last_id = this.page.getId();
		if (last_id <= 0) {
			last_id = this.page.back();
		}
		this.page.close();
		this.page = Pages.getPage(id);
		if (!isBack) {
			this.page.back(last_id);
			this.page.open();
		}
	}

	public void changePage(Page page) {
		if (page == null)
			return;
		int last_id = this.page.getId();
		if (last_id <= 0) {
			last_id = this.page.back();
		}
		this.page.close();
		this.page = page;
		this.page.back(last_id);
		this.page.open();
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		this.page.close();
		super.onContainerClosed(playerIn);
	}

}
