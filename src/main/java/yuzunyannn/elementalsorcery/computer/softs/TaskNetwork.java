package yuzunyannn.elementalsorcery.computer.softs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.DNParams;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGui;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.DeviceScanner;
import yuzunyannn.elementalsorcery.computer.soft.TaskBase;
import yuzunyannn.elementalsorcery.tile.device.DeviceFeature;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class TaskNetwork extends TaskBase {

	public static final String ID = "#NTW";
	protected IDeviceNetwork network;
	protected DDDeviceNetwork ddn = new DDDeviceNetwork();

	public TaskNetwork(IOS os, int pid) {
		super(os, pid);
		// get myself must have
		IDevice device = os.askCapability(null, Computer.DEVICE_CAPABILITY, null).toughGet();
		ddn.setNetwork(this.network = device.getNetwork());
		ddn.onDeviceLinkCangeCallback = udid -> onDeviceLinkChange(udid);
		this.detecter.add("itms", ddn, 10);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	public Map<UUID, DeviceLinkUnitInfo> getNetworkLinkedMap() {
		return ddn.getDataMap();
	}

	@DeviceFeature(id = "handshake")
	public void doHandshake(UUID uuid, CapabilityObjectRef ref) {
		DNParams params = new DNParams();
		params.set(DNParams.args(1), ref);
		IOS os = this.getOS();
		os.notice(null, "network-handshake", params).thenAccept(result -> {
			if (!result.isSuccess()) {
				NBTSender sender = new NBTSender();
				sender.write("code", (byte) -1);
				sender.write("udid", uuid);
				os.message(this, sender.tag());
				return;
			}
		});
	}
	
	@DeviceFeature(id = "disconnect")
	public void doDisconnect(UUID uuid) {
		DNParams params = new DNParams();
		params.set(DNParams.args(1), uuid);
		IOS os = this.getOS();
		os.notice(null, "network-close", params).thenAccept(result -> {
			if (!result.isSuccess()) {
				NBTSender sender = new NBTSender();
				sender.write("code", (byte) -2);
				sender.write("udid", uuid);
				os.message(this, sender.tag());
				return;
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISoftGui createGUIRender() {
		return new TaskNetworkGui(this);
	}

	public void onDeviceLinkChange(UUID udid) {
		NBTSender sender = new NBTSender();
		sender.write("code", (byte) 2);
		sender.write("udid", udid);
		getOS().message(this, sender.tag());
	}

	static public final class ScanCache {
		protected DeviceScanner scanner;
		protected Map<UUID, CapabilityObjectRef> deviceMap = new LinkedHashMap<>();

		public DeviceScanner getScanner() {
			return scanner;
		}

		public void setScanner(DeviceScanner scanner) {
			this.scanner = scanner;
		}

		public Map<UUID, CapabilityObjectRef> getDeviceMap() {
			return deviceMap;
		}
	}

	public ScanCache scanCache;

}
