package yuzunyannn.elementalsorcery.computer.render;

import java.util.UUID;

public class ComputRenderKey {

	private final UUID uuid;
	private final int pid;

	public ComputRenderKey(UUID uuid, int pid) {
		this.uuid = uuid;
		this.pid = pid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof ComputRenderKey) {
			ComputRenderKey other = (ComputRenderKey) obj;
			return this.uuid.equals(other.uuid) && this.pid == other.pid;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.uuid.hashCode() * (this.pid + 1);
	}

}
