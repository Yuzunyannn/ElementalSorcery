package yuzunyannn.elementalsorcery.computer;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IDisk;

public class Disk extends DeviceStorage implements IDisk {

	public Disk() {
	}

	public Disk(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	@Override
	public Disk copy() {
		Disk disk = new Disk();
		disk.deserializeNBT(this.serializeNBT());
		return disk;
	}

}
