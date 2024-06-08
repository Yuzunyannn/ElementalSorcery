package yuzunyannn.elementalsorcery.api.util.detecter;

public interface IDataRef<T> {

	void set(T t);

	T get();

	default T get(T _default) {
		T t = get();
		if (t == null) return _default;
		return t;
	}

	public static class Simple<T> implements IDataRef<T> {

		T obj;

		@Override
		public void set(T t) {
			this.obj = t;
		}

		@Override
		public T get() {
			return this.obj;
		}

	}
}
