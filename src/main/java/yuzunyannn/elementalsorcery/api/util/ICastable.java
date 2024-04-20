package yuzunyannn.elementalsorcery.api.util;

public interface ICastable<T> {

	T cast(Object obj, ICastEnv env);

}
