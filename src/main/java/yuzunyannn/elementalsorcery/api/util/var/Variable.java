package yuzunyannn.elementalsorcery.api.util.var;

public class Variable<T> {

	public final String name;

	public final IVariableType<T> type;

	public Variable(String name, IVariableType<T> type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof Variable) return this.name.equals(((Variable) obj).name);
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
