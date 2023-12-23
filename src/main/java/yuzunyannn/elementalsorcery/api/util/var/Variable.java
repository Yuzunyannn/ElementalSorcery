package yuzunyannn.elementalsorcery.api.util.var;

public class Variable<T> {

	public final String key;

	public final IVariableType<T> type;

	public Variable(String name, IVariableType<T> type) {
		this.key = name;
		this.type = type;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof Variable) return this.key.equals(((Variable) obj).key);
		return false;
	}

	@Override
	public String toString() {
		return key;
	}
}
