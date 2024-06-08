package yuzunyannn.elementalsorcery.api.util;

import javax.annotation.Nullable;

public interface ICastable {

	@Nullable
	<T> T cast(Class<?> to);

}
