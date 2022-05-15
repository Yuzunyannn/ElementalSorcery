package yuzunyannn.elementalsorcery.util;

import java.util.UUID;

import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.VariableSet.Variable;

public class Variables {

	public static final Variable<ElementStack> STORAGE_ELEMENT = new Variable<>("stESk", VariableSet.ELEMENT);
	public static final Variable<UUID> sUUID = new Variable<>("@uuid", VariableSet.UUID);

	public static final Variable<ElementStack> MAGIC;
	public static final Variable<ElementStack> METAL;
	public static final Variable<ElementStack> KNOWLEDGE;
	public static final Variable<ElementStack> WOOD;
	public static final Variable<ElementStack> FIRE;

	static {
		FIRE = new Variable<>("E^?", VariableSet.ELEMENT);
		MAGIC = new Variable<>("E^?", VariableSet.ELEMENT);
		METAL = new Variable<>("E^?", VariableSet.ELEMENT);
		KNOWLEDGE = new Variable<>("E^?", VariableSet.ELEMENT);
		WOOD = new Variable<>("E^?", VariableSet.ELEMENT);
	}

	public static final Variable<Integer> TICK = new Variable<>("tick", VariableSet.INT);

	public static Variable<ElementStack> getElementVar(Element element) {
		return new Variable<>("E^" + element.getRegistryId(), VariableSet.ELEMENT);
	}

}
