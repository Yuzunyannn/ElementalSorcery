package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.util.ISyncWatcher;

public class AuthorityWatcher implements ISyncWatcher {

	public final ISyncWatcher watcher;
	public final String prefix;

	public AuthorityWatcher(ISyncWatcher watcher, String prefix) {
		this.watcher = watcher;
		this.prefix = prefix;
	}

	@Override
	public boolean isLeave() {
		return this.watcher.isLeave();
	}

	@Override
	public <T> T getDetectObject(String key, Class<T> cls) {
		return this.watcher.getDetectObject(prefix + key, cls);
	}

	@Override
	public <T> void setDetectObject(String key, T obj) {
		this.watcher.setDetectObject(prefix + key, obj);
	}

}
