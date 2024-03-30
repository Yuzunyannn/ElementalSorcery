package yuzunyannn.elementalsorcery.api.util.target;

import javax.annotation.Nullable;

public interface IObjectGetter<T> {

	static class Empty implements IObjectGetter {
		@Override
		public Object softGet() {
			return null;
		}

		@Override
		public Object toughGet() {
			return null;
		}
	}

	public static final IObjectGetter EMPTY = new Empty();

	@Nullable
	T softGet();

	@Nullable
	T toughGet();

}
