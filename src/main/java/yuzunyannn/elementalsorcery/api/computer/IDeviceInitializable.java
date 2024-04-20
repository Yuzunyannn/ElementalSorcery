package yuzunyannn.elementalsorcery.api.computer;

import java.util.Collection;
import java.util.UUID;

public interface IDeviceInitializable extends IDevice {

	public static class Init {
		public UUID udid;
		public IDeviceNetwork network;
		public IDeviceInfo info;
		public String name;
		public Collection<UUID> linkers;

		public UUID getUDID() {
			return udid == null ? UUID.randomUUID() : udid;
		}

		public String getName() {
			String n = info != null ? info.getName() : name;
			return n == null ? "" : n;
		}
	}

	public void init(Init init);
}
