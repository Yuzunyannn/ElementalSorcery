package yuzunyannn.elementalsorcery.init;

import yuzunyannn.elementalsorcery.element.Element;

public class ElementRegister extends ESImplRegister<Element> {

	public static final ElementRegister instance = new ElementRegister();

	@Override
	public Class<Element> getRegistrySuperType() {
		return Element.class;
	}

}
