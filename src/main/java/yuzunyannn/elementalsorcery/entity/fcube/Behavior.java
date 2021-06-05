package yuzunyannn.elementalsorcery.entity.fcube;

public class Behavior {

	final String type;
	final String subType;

	public Behavior(String type, String subType) {
		this.type = type;
		this.subType = subType == null ? "" : subType;
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
