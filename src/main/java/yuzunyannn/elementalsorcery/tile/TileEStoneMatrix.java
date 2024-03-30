package yuzunyannn.elementalsorcery.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.IGetItemStack;

public class TileEStoneMatrix extends TileEntityNetworkOld implements IGetItemStack {

	private ItemStack stack = ItemStack.EMPTY;

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack;
		this.updateToClient();
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return this.stack;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("stack")) stack = new ItemStack(compound.getCompoundTag("stack"));
		else stack = ItemStack.EMPTY;
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("stack", stack.serializeNBT());
		return super.writeToNBT(compound);
	}

}
