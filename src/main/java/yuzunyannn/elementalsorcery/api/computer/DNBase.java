package yuzunyannn.elementalsorcery.api.computer;

import java.util.HashMap;

public class DNBase {

	// start with 1
	public static String args(int n) {
		return String.valueOf(n);
	}

	protected HashMap<String, Object> objMap = new HashMap<>();

	public boolean isEmpty() {
		return objMap.isEmpty();
	}

	public void clear() {
		objMap.clear();
	}

	public int size() {
		return objMap.size();
	}

	public <T> void set(String key, T obj) {
		objMap.put(key, obj);
	}

	public <T> T get(String key, Class<T> cls) {
		Object obj = objMap.get(key);
		if (obj == null) return null;
		if (cls.isAssignableFrom(obj.getClass())) return (T) obj;
		return null;
	}

	public <T> T get(String key) {
		return (T) objMap.get(key);
	}

}
