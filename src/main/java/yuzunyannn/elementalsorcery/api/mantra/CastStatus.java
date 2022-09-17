package yuzunyannn.elementalsorcery.api.mantra;

public enum CastStatus {
	ATTACK(-1),
	BEFORE_SPELLING(0),
	SPELLING(1),
	AFTER_SPELLING(2),
	END(3);

	public final byte index;

	CastStatus(int index) {
		this.index = (byte) index;
	}

	static public CastStatus fromIndex(int index) {
		for (CastStatus v : values()) if (v.index == index) return v;
		return BEFORE_SPELLING;
	}

}
