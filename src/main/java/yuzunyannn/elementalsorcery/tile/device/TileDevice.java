package yuzunyannn.elementalsorcery.tile.device;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.computer.DNParams;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerEnvTile;
import yuzunyannn.elementalsorcery.computer.DeviceNetwork;
import yuzunyannn.elementalsorcery.computer.Disk;
import yuzunyannn.elementalsorcery.computer.WideNetwork;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class TileDevice extends TileEntityNetwork implements IDevice {

	protected UUID uuid = UUID.randomUUID();
	protected DeviceNetwork network = new DeviceNetwork(this);
	protected List<IDisk> disks = new ArrayList<>();
	protected String deviceName = "";
	protected IComputEnv env = new ComputerEnvTile(this);
	protected int ticksExisted;

	@Override
	public void writeSaveData(INBTWriter writer) {
		writer.write("uuid", uuid);
		writer.write("network", network);
		writer.write("disks", disks);
		writer.write("name", deviceName);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		uuid = reader.uuid("uuid");
		network = reader.obj("network", network);
		disks = reader.list("disks", nbt -> new Disk(nbt));
		deviceName = reader.string("name");
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writer.write("uuid", uuid);
		writer.write("name", deviceName);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		uuid = reader.uuid("uuid");
		deviceName = reader.string("name");
	}

	@Override
	public CompletableFuture<DNResult> notice(String method, DNParams params) {
		return DNResult.invalid();
	}

	@Override
	public List<IDisk> getDisks() {
		return disks;
	}

	@Override
	public String getName() {
		return deviceName;
	}

	@Override
	public ITextComponent getDisplayName() {
		String name = getName();
		if (name.isEmpty()) return null;
		return new TextComponentString(name);
	}

	@Override
	public UUID getUDID() {
		return uuid;
	}

	@Override
	public IDeviceNetwork getNetwork() {
		return network;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (Computer.DEVICE_CAPABILITY.equals(capability)) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (Computer.DEVICE_CAPABILITY.equals(capability)) return (T) this;
		return super.getCapability(capability, facing);
	}

	public void update() {
		ticksExisted++;
		if (ticksExisted % WideNetwork.SAY_HELLO_INTERVAL == 0) WideNetwork.instance.sayHello(this, env);
		if (world.isRemote) return;
		network.update(env);
	}
}
