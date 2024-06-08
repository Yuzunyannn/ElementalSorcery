package yuzunyannn.elementalsorcery.computer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTSaver;

public class ComputerDevice extends Computer implements ICapabilityProvider {

	protected final Device device;
	protected IComputEnv env;

	public ComputerDevice(String appearance, DeviceInfo info) {
		super(appearance);
		device = new Device(this, info);
	}

	public ComputerDevice(String appearance, ItemStack stack) {
		super(appearance);
		device = new Device(this, new DeviceInfoItem(stack));
	}

	public ComputerDevice setEnv(IComputEnv env) {
		this.env = env;
		this.device.setEnv(env);
		return this;
	}

	@Override
	public IComputEnv getEnv() {
		return env;
	}

	@Override
	public Device device() {
		return device;
	}

	public void addDisk(Disk disk) {
		disks.add(disk);
		this.markDiskValueDirty(false);
	}

	public IDisk removeDisk(int index) {
		IDisk disk = disks.remove(index);
		if (disk != null) this.markDiskValueDirty();
		return disk;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		device.writeSaveData(new NBTSaver(nbt));
		NBTHelper.setNBTSerializableList(nbt, "#D", disks);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		device.readSaveData(new NBTSaver(nbt));
		super.deserializeNBT(nbt);
		disks.clear();
		NBTTagList list = nbt.getTagList("#D", NBTTag.TAG_COMPOUND);
		for (NBTBase n : list) disks.add(new Disk(((NBTTagCompound) n).copy()));
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		NBTTagCompound sendData = super.detectChanges(watcher);

		NBTTagCompound changes = device.detectChanges(watcher);
		if (changes != null) {
			if (sendData == null) sendData = new NBTTagCompound();
			sendData.setTag("#DC", changes);
		}

		return sendData;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		super.mergeChanges(nbt);
		if (nbt.hasKey("#DC")) device.mergeChanges(nbt.getCompoundTag("#DC"));
	}

	@Override
	protected void doUpdate(IComputEnv env) {
		device.update();
		super.doUpdate(env);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (Computer.DEVICE_CAPABILITY.equals(capability)) return true;
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (Computer.DEVICE_CAPABILITY.equals(capability)) return (T) device;
		return null;
	}

}
