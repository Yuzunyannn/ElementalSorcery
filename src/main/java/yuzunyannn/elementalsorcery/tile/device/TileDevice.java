package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.Device;
import yuzunyannn.elementalsorcery.computer.DeviceInfoTile;
import yuzunyannn.elementalsorcery.computer.DeviceProcess;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class TileDevice extends TileEntityNetwork {

	protected int ticksExisted;
	protected final Device device;
	protected final DeviceProcess process;

	public TileDevice() {
		this.device = new Device(this, new DeviceInfoTile(this));
		this.process = this.device.getProcess();
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		device.writeSaveData(writer);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		device.readSaveData(reader);
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		device.writeUpdateData(writer);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		device.readUpdateData(reader);
	}

	@Override
	public ITextComponent getDisplayName() {
		String name = device.getName();
		if (name.isEmpty()) return null;
		return new TextComponentString(name);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (Computer.DEVICE_CAPABILITY.equals(capability)) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (Computer.DEVICE_CAPABILITY.equals(capability)) return (T) device;
		return super.getCapability(capability, facing);
	}

	public void update() {
		ticksExisted++;
		device.update();
	}
}
