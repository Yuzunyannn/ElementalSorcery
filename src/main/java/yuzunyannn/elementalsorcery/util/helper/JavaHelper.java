package yuzunyannn.elementalsorcery.util.helper;

public class JavaHelper {

	public static boolean isTrue(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Boolean) return (Boolean) (obj);
		return true;
	}

}
