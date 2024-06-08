package yuzunyannn.elementalsorcery.api.computer;

import java.util.Arrays;
import java.util.Iterator;

import yuzunyannn.elementalsorcery.api.util.ICastEnv;
import yuzunyannn.elementalsorcery.api.util.ICastHandler;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class DeviceFilePath implements Iterable<String> {

	protected static String[] split(String path) {
		path = path.replace('\\', '/').replace("./", "").trim();
		return path.split("/");
	}

	public static DeviceFilePath parse(String path) {
		String[] strs = split(path);
		return of(strs);
	}

	public static DeviceFilePath of(String... names) {
		return new DeviceFilePath(names);
	}

	public static String extname(String name) {
		int index = name.lastIndexOf(".");
		if (index == -1) return "";
		return name.substring(index);
	}

	protected final String[] paths;
	protected final String full;

	public DeviceFilePath(String[] paths) {
		this.paths = paths;
		this.full = String.join("/", paths);
	}

	public String[] toStrings() {
		return paths;
	}

	public int length() {
		return paths.length;
	}

	public boolean isEmpty() {
		return paths.length == 0;
	}

	public String getName() {
		if (this.paths.length == 0) return "";
		return this.paths[this.paths.length - 1];
	}

	public String get(int index) {
		return this.paths[index];
	}

	public DeviceFilePath sub(int index) {
		if (index >= this.paths.length - 1) return this;
		return new DeviceFilePath(Arrays.copyOfRange(this.paths, 0, index + 1));
	}

	public DeviceFilePath prepend(String... strs) {
		return of(JavaHelper.concat(strs, paths));
	}

	public DeviceFilePath append(String... strs) {
		return of(JavaHelper.concat(paths, strs));
	}

	@Override
	public String toString() {
		return this.full;
	}

	@Override
	public int hashCode() {
		return this.full.hashCode();
	}

	@Override
	public Iterator<String> iterator() {
		return Arrays.stream(paths).iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof DeviceFilePath) {
			DeviceFilePath other = (DeviceFilePath) obj;
			return this.full.equals(other.full);
		}
		return false;
	}

	public static class Cast implements ICastHandler<DeviceFilePath> {

		@Override
		public DeviceFilePath cast(Object obj, ICastEnv env) {
			if (obj instanceof String) return DeviceFilePath.parse(obj.toString());
			return null;
		}

	}

}
