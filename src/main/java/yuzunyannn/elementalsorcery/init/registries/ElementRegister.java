package yuzunyannn.elementalsorcery.init.registries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.registries.IForgeRegistry;
import yuzunyannn.elementalsorcery.api.element.Element;

public class ElementRegister implements IForgeRegistry<Element> {

	public static final ElementRegister instance = new ElementRegister();

	private static final Registry REGISTRY = new Registry();

	private static class Registry extends RegistryNamespaced<ResourceLocation, Element> {
		public List<Element> getValues() {
			return new ArrayList<Element>(registryObjects.values());
		}

		public Set<Entry<ResourceLocation, Element>> getEntries() {
			return registryObjects.entrySet();
		}
	}

	private int nId = 0;

	@Override
	public void register(Element element) {
		REGISTRY.register(nId++, element.getRegistryName(), element);
	}

	@Override
	public Iterator<Element> iterator() {
		return REGISTRY.iterator();
	}

	@Override
	public Class<Element> getRegistrySuperType() {
		return Element.class;
	}

	@Override
	public void registerAll(Element... values) {
		for (int i = 0; i < values.length; i++) register(values[i]);
	}

	@Override
	public boolean containsKey(ResourceLocation key) {
		return REGISTRY.containsKey(key);
	}

	@Override
	public boolean containsValue(Element value) {
		return REGISTRY.getIDForObject(value) != -1;
	}

	@Override
	public Element getValue(ResourceLocation name) {
		return REGISTRY.getObject(name);
	}

	@Override
	public ResourceLocation getKey(Element value) {
		return REGISTRY.getNameForObject(value);
	}

	@Override
	public Set<ResourceLocation> getKeys() {
		return REGISTRY.getKeys();
	}

	public int getId(Element value) {
		return REGISTRY.getIDForObject(value);
	}

	@Override
	public List<Element> getValues() {
		return REGISTRY.getValues();
	}

	@Override
	public Set<Entry<ResourceLocation, Element>> getEntries() {
		return REGISTRY.getEntries();
	}

	@Override
	public <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type) {
		return null;
	}

}
