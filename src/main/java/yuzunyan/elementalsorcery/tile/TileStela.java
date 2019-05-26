package yuzunyan.elementalsorcery.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileStela extends TileEntityNetwork {

	private EnumFacing face = EnumFacing.NORTH;

	public void setFace(EnumFacing face) {
		this.face = face;
	}

	public EnumFacing getFace() {
		return face;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		face = EnumFacing.values()[compound.getByte("face")];
		this.inv_item.deserializeNBT(compound.getCompoundTag("inv_item"));
		this.inv_paper.deserializeNBT(compound.getCompoundTag("inv_paper"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setByte("face", (byte) face.ordinal());
		compound.setTag("inv_item", this.inv_item.serializeNBT());
		compound.setTag("inv_paper", this.inv_paper.serializeNBT());
		return super.writeToNBT(compound);
	}

	protected ItemStackHandler inv_item = new ItemStackHandler(1);
	protected ItemStackHandler inv_paper = new ItemStackHandler(1);

	// 拥有能力
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	// 获取能力
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == face.getOpposite())
				return (T) inv_item;
			return (T) inv_paper;
		}
		return super.getCapability(capability, facing);
	}
}
