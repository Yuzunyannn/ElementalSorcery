package yuzunyannn.elementalsorcery.util;

public class MultiRets {

	public static MultiRets ret(Object... objects) {
		return new MultiRets(objects);
	}

	public final Object[] objs;

	public MultiRets(Object... objects) {
		objs = objects;
	}

	public boolean isEmpty() {
		return objs.length == 0;
	}

	public <T> T get(int index, Class<T> cls) {
		if (index < 0 || index >= objs.length) return null;
		Object obj = objs[index];
		if (obj != null && cls.isAssignableFrom(obj.getClass())) return (T) obj;
		return null;
	}

}
