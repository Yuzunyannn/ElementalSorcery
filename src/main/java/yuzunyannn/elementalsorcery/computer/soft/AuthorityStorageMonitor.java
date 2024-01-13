package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.StoragePath;
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
		StoragePath path = StoragePath.of(JavaHelper.concat(this.paths, pathstrs));
		this.monitor.add(path);
	}

	@Override
	public void add(StoragePath path) {
		this.monitor.add(path.addFront(this.paths));
	}

	@Override
	public void remove(StoragePath path) {
		this.monitor.remove(path.addFront(this.paths));
	}

	@Override
	public void markDirty(StoragePath path) {
		this.monitor.markDirty(path.addFront(this.paths));
	}

}
