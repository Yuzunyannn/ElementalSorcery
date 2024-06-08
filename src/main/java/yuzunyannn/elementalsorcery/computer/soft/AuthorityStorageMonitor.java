package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.computer.IStorageMonitor;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class AuthorityStorageMonitor implements IStorageMonitor {

	public final IStorageMonitor monitor;
	public final String[] paths;

	public AuthorityStorageMonitor(IStorageMonitor other, String... paths) {
		this.monitor = other;
		this.paths = paths;
	}

	@Override
	public void add(String... pathstrs) {
		DeviceFilePath path = DeviceFilePath.of(JavaHelper.concat(this.paths, pathstrs));
		this.monitor.add(path);
	}

	@Override
	public void add(DeviceFilePath path) {
		this.monitor.add(path.prepend(this.paths));
	}

	@Override
	public void remove(DeviceFilePath path) {
		this.monitor.remove(path.prepend(this.paths));
	}

	@Override
	public void markDirty(DeviceFilePath path) {
		this.monitor.markDirty(path.prepend(this.paths));
	}

}
