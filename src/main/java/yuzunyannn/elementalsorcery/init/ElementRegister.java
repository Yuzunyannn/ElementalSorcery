package yuzunyannn.elementalsorcery.init;

import yuzunyannn.elementalsorcery.element.Element;

public class ElementRegister {

	private static void register(Element element) {
		Element.REGISTRY.register(element);
	}

	static public void registerAll() {
		register(ESInitInstance.ELEMENTS.VOID);
		register(ESInitInstance.ELEMENTS.MAGIC);
		register(ESInitInstance.ELEMENTS.ENDER);
		register(ESInitInstance.ELEMENTS.FIRE);
		register(ESInitInstance.ELEMENTS.WATER);
		register(ESInitInstance.ELEMENTS.AIR);
		register(ESInitInstance.ELEMENTS.EARTH);
		register(ESInitInstance.ELEMENTS.METAL);
		register(ESInitInstance.ELEMENTS.WOOD);
		register(ESInitInstance.ELEMENTS.KNOWLEDGE);
	}
}
