package yuzunyannn.elementalsorcery.util;

public class LambdaReference<T> {

	static public <T> LambdaReference of(T obj) {
		LambdaReference ref = new LambdaReference();
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
