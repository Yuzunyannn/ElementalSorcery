package yuzunyan.elementalsorcery.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

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
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setByte("face", (byte) face.ordinal());
		return super.writeToNBT(compound);
	}

}
