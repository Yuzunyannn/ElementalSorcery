package yuzunyannn.elementalsorcery.util.detecter;

import java.util.HashMap;

import yuzunyannn.elementalsorcery.api.util.ISyncWatcher;

public abstract class SyncWatcher implements ISyncWatcher {

	protected HashMap<String, Object> objMap = new HashMap<>();

	@Override
	public <T> void setDetectObject(String key, T obj) {
		objMap.put(key, obj);
	}

	@Override
	public <T> T getDetectObject(String key, Class<T> cls) {
		Object obj = objMap.get(key);
		if (obj == null) return null;
		if (cls.isAssignableFrom(obj.getClass())) return (T) obj;
		return null;
	}

}
