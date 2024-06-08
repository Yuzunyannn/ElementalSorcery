package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.DiskItem;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ComputerTile extends Computer {

	protected final TileComputer tile;

	public ComputerTile(TileComputer computer) {
		super(computer.getAppearance());
		this.tile = computer;
	}

	@Override
	public IDevice device() {
		return this.tile.device;
	}

	@Override
	public IComputEnv getEnv() {
		return this.tile.myEnv;
	}

	public void addDisk(DiskItem disk) {
		disks.add(disk);
		this.markDiskValueDirty(false);
	}

	public void setDisk(int index, DiskItem disk) {
		disks.set(index, disk);
		this.markDiskValueDirty();
	}

	public IDisk removeDisk(int index) {
		IDisk disk = disks.remove(index);
		if (disk != null) this.markDiskValueDirty(false);
		return disk;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		NBTHelper.setNBTSerializableList(nbt, "#D", disks);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		disks.clear();
		NBTTagList list = nbt.getTagList("#D", NBTTag.TAG_COMPOUND);
		for (NBTBase n : list) {
			DiskItem disk = new DiskItem(((NBTTagCompound) n).copy());
			if (disk.isEmpty()) continue;
			disks.add(disk);
		}
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		NBTTagCompound sendData = super.detectChanges(watcher);

		NBTTagCompound changes = tile.device.detectChanges(watcher);
		if (changes != null) {
			if (sendData == null) sendData = new NBTTagCompound();
			sendData.setTag("#DC", changes);
		}

		return sendData;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		super.mergeChanges(nbt);
		if (nbt.hasKey("#DC")) tile.device.mergeChanges(nbt.getCompoundTag("#DC"));
	}

}
