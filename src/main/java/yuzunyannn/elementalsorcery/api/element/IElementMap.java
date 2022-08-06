package yuzunyannn.elementalsorcery.api.element;

import java.util.Collection;

import yuzunyannn.elementalsorcery.api.crafting.IToElement;

public interface IElementMap extends IToElement {

	void add(IToElement toElement);

	void add(int index, IToElement toElement);

	Collection<IToElement> getToElements();

}
