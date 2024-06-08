package yuzunyannn.elementalsorcery.api.util;

public interface ICastHandler<T> {

	T cast(Object obj, ICastEnv env);

}
