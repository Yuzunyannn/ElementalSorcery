package yuzunyannn.elementalsorcery.item.device;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESStorageKeyEnum;
import yuzunyannn.elementalsorcery.computer.DiskItem;
import yuzunyannn.elementalsorcery.computer.soft.EOS;
import yuzunyannn.elementalsorcery.computer.softs.AppCommand;

public class ItemDisk extends Item {

	public ItemDisk() {
		this.setTranslationKey("disk");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			ItemStack itemStack = new ItemStack(this);
			items.add(EOS.setBoot(new DiskItem(itemStack), AppCommand.ID).getItemStack());
		}
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		stack.getOrCreateSubCompound(ESStorageKeyEnum.DISK_DATA);
	}

}
