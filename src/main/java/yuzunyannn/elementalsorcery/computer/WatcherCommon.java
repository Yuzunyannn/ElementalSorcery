package yuzunyannn.elementalsorcery.computer;

import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;

public abstract class WatcherCommon implements IComputerWatcher {

	protected Object obj;

	@Override
	public <T> void setDetectObject(T obj) {
		this.obj = obj;
	}

	@Override
	public <T> T getDetectObject(Class<T> cls) {
		if (obj == null) return null;
		if (cls.isAssignableFrom(obj.getClass())) return (T) obj;
		return null;
	}

}
