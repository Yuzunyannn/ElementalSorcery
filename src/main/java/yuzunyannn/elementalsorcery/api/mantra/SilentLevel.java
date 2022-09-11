package yuzunyannn.elementalsorcery.api.mantra;

public enum SilentLevel {
	/** 无法吟唱 */
	SPELL(1),
	
	/** 无法释放 */
	RELEASE(2),
	
	/** 魔法现象 */
	PHENOMENON(3);

	public final byte lev;

	SilentLevel(int lev) {
		this.lev = (byte) lev;
	}
}
