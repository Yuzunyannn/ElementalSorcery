package yuzunyannn.elementalsorcery.init.registries;

import yuzunyannn.elementalsorcery.api.element.Element;

public class ElementRegister extends ESImplRegister<Element> {

	public static final ElementRegister instance = new ElementRegister();

	@Override
	public Class<Element> getRegistrySuperType() {
		return Element.class;
	}

}
