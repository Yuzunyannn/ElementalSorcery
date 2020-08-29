package yuzunyannn.elementalsorcery.init;

import yuzunyannn.elementalsorcery.element.Element;

public class ElementRegister extends ESImplRegister<Element> {

	public static final ElementRegister instance = new ElementRegister();

	@Override
	public Class<Element> getRegistrySuperType() {
		return Element.class;
	}

	private static void registerElement(Element element) {
		ElementRegister.instance.register(element);
	}

	static public void registerAll() {
		registerElement(ESInitInstance.ELEMENTS.VOID);
		registerElement(ESInitInstance.ELEMENTS.MAGIC);
		registerElement(ESInitInstance.ELEMENTS.ENDER);
		registerElement(ESInitInstance.ELEMENTS.FIRE);
		registerElement(ESInitInstance.ELEMENTS.WATER);
		registerElement(ESInitInstance.ELEMENTS.AIR);
		registerElement(ESInitInstance.ELEMENTS.EARTH);
		registerElement(ESInitInstance.ELEMENTS.METAL);
		registerElement(ESInitInstance.ELEMENTS.WOOD);
		registerElement(ESInitInstance.ELEMENTS.KNOWLEDGE);
	}
}
