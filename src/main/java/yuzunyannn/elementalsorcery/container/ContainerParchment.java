package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ContainerParchment extends Container {

	public final EntityPlayer player;
	public final ItemStack heldItem;

	public ContainerParchment(EntityPlayer player) {
		this.player = player;
		this.heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		ItemStack stack = playerIn.getHeldItem(EnumHand.MAIN_HAND);
		return stack == this.heldItem;
	}

}
