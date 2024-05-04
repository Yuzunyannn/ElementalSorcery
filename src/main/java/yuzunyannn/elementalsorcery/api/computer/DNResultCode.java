package yuzunyannn.elementalsorcery.api.computer;

public enum DNResultCode {
	// can handle but refuse this request
	REFUSE,
	// is invalid, means cannot handle this request
	INVALID,
	// handled success
	SUCCESS,
	// handled fail
	FAIL,
	// now is invalid maybe valid in furture
	UNAVAILABLE;

	public static DNResultCode fromMeta(int meta) {
		return DNResultCode.values()[meta % DNResultCode.values().length];
	}

	public int getMeta() {
		return ordinal();
	}
}
