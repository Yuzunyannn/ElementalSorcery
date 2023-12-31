package yuzunyannn.elementalsorcery.api.computer;

import java.util.Arrays;

import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class StoragePath {

	public static StoragePath of(String... names) {
		return new StoragePath(names);
	}

	protected final String[] paths;
	protected final String full;

	public StoragePath(String[] paths) {
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

	public String get(int index) {
		return this.paths[index];
	}

	public StoragePath sub(int index) {
		if (index >= this.paths.length - 1) return this;
		return new StoragePath(Arrays.copyOfRange(this.paths, 0, index + 1));
	}

	public StoragePath addFront(String... strs) {
		return of(JavaHelper.concat(strs, paths));
	}

	@Override
	public int hashCode() {
		return this.full.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof StoragePath) {
			StoragePath other = (StoragePath) obj;
			return this.full.equals(other.full);
		}
		return false;
	}

}
