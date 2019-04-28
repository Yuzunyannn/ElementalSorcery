package yuzunyan.elementalsorcery.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.init.ESInitInstance;
import yuzunyan.elementalsorcery.parchment.Page;

public class ItemScroll extends Item {

	static public ItemStack getOrigin() {
		return new ItemStack(ESInitInstance.ITEMS.SCROLL);
	}

	public ItemScroll() {
		this.setUnlocalizedName("scroll");
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		NBTTagCompound nbt = stack.getOrCreateSubCompound("scroll");
		int id = 1;
		if (nbt.hasKey("pre_id")) {
			id = nbt.getInteger("pre_id") + 1;
			id = id % Page.getMax();
			if (id == 0)
				id++;
			nbt.setInteger("pre_id", id);
		} else
			nbt.setInteger("pre_id", id);
		playerIn.inventory.addItemStackToInventory(ItemParchment.getParchment(id));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

}
