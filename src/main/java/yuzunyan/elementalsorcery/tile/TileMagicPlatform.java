package yuzunyan.elementalsorcery.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyan.elementalsorcery.api.ability.IGetItemStack;

public class TileMagicPlatform extends TileEntityNetwork implements IGetItemStack {
	private ItemStack stack = ItemStack.EMPTY;

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack;
		roate_begin = (float) Math.random() * 360;
		this.updateToClient();
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return this.stack;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("stack"))
			stack = new ItemStack(compound.getCompoundTag("stack"));
		else
			stack = ItemStack.EMPTY;
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("stack", stack.serializeNBT());
		return super.writeToNBT(compound);
	}

	public float roate_begin = (float) Math.random() * 360;

}
