package yuzunyannn.elementalsorcery.util;

public class LamdaReference<T> {

	static public <T> LamdaReference of(T obj) {
		LamdaReference ref = new LamdaReference();
		ref.obj = obj;
		return ref;
	}

	public T obj;

	public T get() {
		return obj;
	}

	public void set(T obj) {
		this.obj = obj;
	}
}
