package yuzunyannn.elementalsorcery.computer;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IDisk;

public class Disk implements IDisk {

	NBTTagCompound context;

	public Disk() {
		this.context = new NBTTagCompound();
	}

	public Disk(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public NBTTagCompound getContext() {
		return context;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return context.copy();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		context = nbt;
	}
}
