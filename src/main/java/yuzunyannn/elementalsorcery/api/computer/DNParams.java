package yuzunyannn.elementalsorcery.api.computer;

import javax.annotation.Nullable;

public class DNParams extends DNBase {

	public static final DNParams EMPTY = new DNParams(null) {
		@Override
		public <T> void set(String key, T obj) {
		}

		@Override
		public <T> T get(String key, Class<T> cls) {
			return null;
		}
	};

	protected IDevice src;

	public DNParams() {
	}

	public DNParams(IDevice from) {
		this.src = from;
	}

	@Nullable
	public IDevice getSrcDevice() {
		return src;
	}

}