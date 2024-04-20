package yuzunyannn.elementalsorcery.computer.softs;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.computer.Computer;

public final class DeviceLinkUnitInfo {

	public static final int STATUS_UNLINK = 0;
	public static final int STATUS_WAITING = 1;

	public static final int STATUS_CONNECT = 2;
	public static final int STATUS_RECONNECTING = 3;
	public static final int STATUS_DISCONNECTING = 4;
	
	public DeviceLinkUnitInfo(UUID udid) {
		this.status = STATUS_UNLINK;
		this.ref = CapabilityObjectRef.INVALID;
		this.udid = udid;
	}

	public DeviceLinkUnitInfo(UUID udid, CapabilityObjectRef ref) {
		this.status = STATUS_UNLINK;
		this.ref = ref;
		this.udid = udid;
	}

	public DeviceLinkUnitInfo(IDeviceLinker linker) {
		this.status = linker.isConnecting() ? STATUS_CONNECT : STATUS_RECONNECTING;
		this.ref = linker.getRemoteRef();
		this.udid = linker.getRemoteUUID();
	}

	public final UUID udid;
	protected int status;
	protected CapabilityObjectRef ref;

	public DeviceLinkUnitInfo copy() {
		DeviceLinkUnitInfo info = new DeviceLinkUnitInfo(udid, ref);
		info.status = this.status;
		return info;
	}

	public int getStatus() {
		return status;
	}

	public boolean isInLink() {
		return status != STATUS_UNLINK && status != STATUS_WAITING;
	}

	public boolean isConnect() {
		return STATUS_CONNECT == status;
	}

	@Nonnull
	public CapabilityObjectRef getRef() {
		return ref;
	}

	public IDevice getDevice() {
		return ref.getCapability(Computer.DEVICE_CAPABILITY, null);
	}

	public boolean checkRefIsValid() {
		if (!ref.checkReference()) return false;
		IDevice device = ref.getCapability(Computer.DEVICE_CAPABILITY, null);
		if (device == null) return false;
		if (!udid.equals(device.getUDID())) return false;
		return true;
	}

	public Vec3d getPositionVec() {
		IWorldObject wo = ref.toWorldObject();
		return wo == null ? null : wo.getObjectPosition();
	}
}
