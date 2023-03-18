package yuzunyannn.elementalsorcery.util.helper;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.function.Function;

public class JavaHelper {

	public static boolean isTrue(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Boolean) return (Boolean) (obj);
		return true;
	}
//
//	public static <T, U> T[] toArray(Collection<U> list, Function<U, T> func) {
//		T[] array = (T[]) Array.newInstance(Object.class, list.size());
//		if (list.isEmpty()) return array;
//		int index = 0;
//		for (U obj : list) array[index++] = func.apply(obj);
//		return array;
//	}

}
