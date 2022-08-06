package yuzunyannn.elementalsorcery.api.entity;

public class Behavior {

	final String type;
	final String subType;
	final int priority;

	public Behavior(String type, String subType, int priority) {
		this.type = type;
		this.subType = subType == null ? "" : subType;
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public boolean is(String type) {
		return this.type.equals(type);
	}

	public boolean is(String type, String subType) {
		return this.type.equals(type) && this.subType.equals(subType);
	}

	public <T extends Behavior> T to(Class<T> cls) {
		if (cls.isAssignableFrom(this.getClass())) return (T) this;
		return null;
	}

	@Override
	public String toString() {
		return type + ":" + subType;
	}
}
