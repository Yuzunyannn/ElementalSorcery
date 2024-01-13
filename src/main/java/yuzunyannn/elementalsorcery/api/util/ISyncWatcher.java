package yuzunyannn.elementalsorcery.api.util;

import java.util.function.Supplier;

public interface ISyncWatcher {

	boolean isLeave();

	<T> T getDetectObject(String key, Class<T> cls);

	<T> void setDetectObject(String key, T obj);

	default <T> T getOrCreateDetectObject(String key, Class<T> cls, Supplier<T> factory) {
		T obj = getDetectObject(key, cls);
		if (obj == null) setDetectObject(key, obj = factory.get());
		return obj;

	}

}
