package yuzunyannn.elementalsorcery.util.helper;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

public class JavaHelper {

	public static void clipboardWrite(String str) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(str), null);
	}

	public static String clipboardRead() {
		Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
				return (String) transferable.getTransferData(DataFlavor.stringFlavor);
			return "";
		} catch (Exception e) {
			return "";
		}
	}

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

	public static String[] concat(String[] a, String[] b) {
		String[] newArray = new String[a.length + b.length];
		System.arraycopy(a, 0, newArray, 0, a.length);
		System.arraycopy(b, 0, newArray, a.length, b.length);
		return newArray;
	}

	public static <T, U> Entry<T, U> entry(T t, U u) {
		return new AbstractMap.SimpleEntry<T, U>(t, u);
	}

	public static void write(byte[] bytes, int offset, long l) {
		for (int i = 0; i < 8; i++) bytes[offset + i] = (byte) (l >> i * 8);
	}

	public static long readLong(byte[] bytes, int offset) {
		long l = 0;
		for (int i = 0; i < 8; i++) l |= (long) (bytes[offset + i] & 0xFF) << i * 8;
		return l;
	}

	public static <T, U> ArrayList<T> toList(Collection<U> objs, Function<U, T> mapper) {
		ArrayList<T> list = new ArrayList<>();
		for (U obj : objs) {
			T ret = mapper.apply(obj);
			if (ret != null) list.add(ret);
		}
		return list;
	}

//	public static <T, U> T[] toArray(Collection<U> list, Function<U, T> func) {
//		T[] array = (T[]) Array.newInstance(Object.class, list.size());
//		if (list.isEmpty()) return array;
//		int index = 0;
//		for (U obj : list) array[index++] = func.apply(obj);
//		return array;
//	}

}
