package yuzunyannn.elementalsorcery.util;

import java.io.Closeable;
import java.io.IOException;

public class IOHelper {
	
	public static void closeQuietly(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException ignored) {}
		}
	}
	
}
