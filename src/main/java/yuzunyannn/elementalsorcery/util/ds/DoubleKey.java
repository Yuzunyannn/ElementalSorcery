package yuzunyannn.elementalsorcery.util.ds;

public class DoubleKey<K1, K2> {

	static public <K1, K2> DoubleKey<K1, K2> of(K1 k1, K2 k2) {
		return new DoubleKey(k1, k2);
	}

	protected final K1 a;
	protected final K2 b;

	public DoubleKey(K1 a, K2 b) {
		this.a = a;
		this.b = b;
		assert a != null : "key1 is null";
		assert b != null : "key2 is null";
	}

	public K1 getKey1() {
		return a;
	}

	public K2 getKey2() {
		return b;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof DoubleKey) {
			DoubleKey other = (DoubleKey) obj;
			return this.a.equals(other.a) && this.b.equals(other.b);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.a.hashCode() + this.b.hashCode() * 133;
	}

}
