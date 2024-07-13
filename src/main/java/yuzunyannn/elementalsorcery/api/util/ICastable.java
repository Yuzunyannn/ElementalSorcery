package yuzunyannn.elementalsorcery.api.util;

import javax.annotation.Nullable;

public interface ICastable {

	public static final ICastable EMPTY = new ICastable() {
		@Override
		public <T> T cast(Class<?> to) {
			return null;
		}
	};

	@Nullable
	<T> T cast(Class<?> to);

}
