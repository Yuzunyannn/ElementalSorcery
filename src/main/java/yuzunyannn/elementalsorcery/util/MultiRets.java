package yuzunyannn.elementalsorcery.util;

public class MultiRets {

	public static final MultiRets EMPTY = new MultiRets();

	public static MultiRets ret(Object... objects) {
		if (objects.length == 0) return EMPTY;
		return new MultiRets(objects);
	}

	public final Object[] objs;

	public MultiRets(Object... objects) {
		objs = objects;
	}

	public boolean isEmpty() {
		return objs.length == 0;
	}

	public int size() {
		return objs.length;
	}

	public <T> T get(int index, Class<T> cls) {
		if (index < 0 || index >= objs.length) return null;
		Object obj = objs[index];
		if (obj != null && cls.isAssignableFrom(obj.getClass())) return (T) obj;
		return null;
	}

	public <T> T get(int index, T defaultRet) {
		if (index < 0 || index >= objs.length) return defaultRet;
		Object obj = objs[index];
		if (obj != null && defaultRet.getClass().isAssignableFrom(obj.getClass())) return (T) obj;
		return defaultRet;
	}

	public <T extends Number> T getNumber(int index, Class<T> cls) {
		if (index < 0 || index >= objs.length) return null;
		Object obj = objs[index];
		if (obj instanceof Number) {
			Number num = (Number) obj;
			if (cls == Integer.class) return (T) Integer.valueOf(num.intValue());
			else if (cls == Float.class) return (T) Float.valueOf(num.floatValue());
			else if (cls == Byte.class) return (T) Byte.valueOf(num.byteValue());
			else if (cls == Short.class) return (T) Short.valueOf(num.shortValue());
			else if (cls == Double.class) return (T) Double.valueOf(num.doubleValue());
			else if (cls == Long.class) return (T) Long.valueOf(num.longValue());
		}
		return null;
	}

}
