package yuzunyannn.elementalsorcery.init;

import yuzunyannn.elementalsorcery.element.Element;

public class ElementRegister {

	private static void register(Element element) {
		Element.REGISTRY.register(element);
	}

	static public void registerAll() {
		register(ESInit.ELEMENTS.VOID);
		register(ESInit.ELEMENTS.MAGIC);
		register(ESInit.ELEMENTS.ENDER);
		register(ESInit.ELEMENTS.FIRE);
		register(ESInit.ELEMENTS.WATER);
		register(ESInit.ELEMENTS.AIR);
		register(ESInit.ELEMENTS.EARTH);
		register(ESInit.ELEMENTS.METAL);
		register(ESInit.ELEMENTS.WOOD);
		register(ESInit.ELEMENTS.KNOWLEDGE);
	}
}
