package yuzunyannn.elementalsorcery.util.helper;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

public class JavaHelper {

	public static boolean isTrue(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Boolean) return (Boolean) (obj);
		return true;
	}

	public static <T extends Comparable<T>> void orderAdd(List<T> list, T obj) {
		int i = MathSupporter.binarySearch(list, (s) -> (double) s.compareTo(obj));
		if (i < 0) i = -i - 1;
		list.add(i, obj);
	}

	public static Object getFieldValue(Object obj, String fieldName) {
		try {
			Field field = ObfuscationReflectionHelper.findField(obj.getClass(), fieldName);
			return field.get(obj);
		} catch (Exception e) {}
		return null;
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
