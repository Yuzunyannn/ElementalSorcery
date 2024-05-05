package yuzunyannn.elementalsorcery.api.computer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DeviceNetworkRoute {

	protected List<UUID> chain;
	protected int cursor;

	public DeviceNetworkRoute(UUID udid) {
		if (udid != null) {
			chain = new ArrayList<>(8);
			chain.add(udid);
		}
	}

	public boolean isLocal() {
		return chain == null || cursor >= chain.size();
	}

	public UUID next() {
		if (chain == null) return null;
		if (cursor >= chain.size()) return null;
		return chain.get(cursor++);
	}
}
