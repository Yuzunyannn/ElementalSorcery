package yuzunyannn.elementalsorcery.api.util.detecter;

public interface IDataRef<T> {

	void set(T t);

	T get();

	default T get(T _default) {
		T t = get();
		if (t == null) return _default;
		return t;
	}

}
