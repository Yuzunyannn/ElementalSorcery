package yuzunyannn.elementalsorcery.computer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.util.render.GameDisplayCast;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayable;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;

public class DeviceLinkerSelf implements IDeviceLinker, IDisplayable {

	protected final IDeviceNetwork network;
	protected final IDevice device;

	public DeviceLinkerSelf(IDeviceNetwork network) {
		this.network = network;
		this.device = network.getDevice();
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public boolean isClose() {
		return false;
	}

	@Override
	public boolean isConnecting() {
		return true;
	}

	@Override
	public void close() {
	}

	@Override
	public UUID getRemoteUUID() {
		return device.getUDID();
	}

	@Override
	public IDevice getRemoteDevice() {
		return device;
	}

	@Override
	public CapabilityObjectRef getRemoteRef() {
		IDeviceEnv env = device.getEnv();
		return env == null ? CapabilityObjectRef.INVALID : env.createRef();
	}

	@Override
	public boolean reconnect(IDeviceEnv env) {
		if (isClose()) return false;
		return true;
	}

	@Override
	public boolean reconnectByOther(IDeviceEnv otherEnv) {
		if (isClose()) return false;
		return true;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return null;
	}

	@Override
	public Object toDisplayObject() {
		List<Object> list = new ArrayList<>();
		list.add("-------");
		list.add(device.getUDID().toString());
		list.add(new TextComponentTranslation("es.app.status", "*"));
		return list;
	}

}
