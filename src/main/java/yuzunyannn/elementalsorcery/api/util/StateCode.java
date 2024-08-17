package yuzunyannn.elementalsorcery.api.util;

public enum StateCode {

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

	public static StateCode fromMeta(int meta) {
		return StateCode.values()[meta % StateCode.values().length];
	}

	public int getMeta() {
		return ordinal();
	}
}
