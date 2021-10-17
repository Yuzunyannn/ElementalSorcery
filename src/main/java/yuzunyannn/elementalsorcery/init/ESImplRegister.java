package yuzunyannn.elementalsorcery.init;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ESImplRegister<T extends IForgeRegistryEntry<T>> implements IForgeRegistry<T> {

	private final Registry REGISTRY = new Registry();

	private class Registry extends RegistryNamespaced<ResourceLocation, T> {
		public List<T> getValues() {
			return new ArrayList<T>(registryObjects.values());
		}

		public Set<Entry<ResourceLocation, T>> getEntries() {
			return registryObjects.entrySet();
		}
	}

	private int nId = 0;
	private Class<T> superType;

	public ESImplRegister(Class<T> cls) {
		this.superType = cls;
	}

	@Override
	public Class<T> getRegistrySuperType() {
		return superType;
	}

	@Override
	public void register(T value) {
		if (REGISTRY.containsKey(value.getRegistryName()))
			throw new RuntimeException("Duplicate ID : " + value.getRegistryName());
		REGISTRY.register(nId++, value.getRegistryName(), value);
	}

	@Override
	public Iterator<T> iterator() {
		return REGISTRY.iterator();
	}

	@Override
	public void registerAll(T... values) {
		for (int i = 0; i < values.length; i++) register(values[i]);
	}

	@Override
	public boolean containsKey(ResourceLocation key) {
		return REGISTRY.containsKey(key);
	}

	@Override
	public boolean containsValue(T value) {
		return REGISTRY.getIDForObject(value) != -1;
	}

	@Override
	public T getValue(ResourceLocation name) {
		return REGISTRY.getObject(name);
	}

	public T getValue(int id) {
		return REGISTRY.getObjectById(id);
	}

	@Override
	public ResourceLocation getKey(T value) {
		return REGISTRY.getNameForObject(value);
	}

	@Override
	public Set<ResourceLocation> getKeys() {
		return REGISTRY.getKeys();
	}

	public int getId(T value) {
		return REGISTRY.getIDForObject(value);
	}

	@Override
	public List<T> getValues() {
		return REGISTRY.getValues();
	}

	@Override
	public Set<Entry<ResourceLocation, T>> getEntries() {
		return REGISTRY.getEntries();
	}

	@Override
	public <U> U getSlaveMap(ResourceLocation slaveMapName, Class<U> type) {
		return null;
	}

	@Nullable
	public T getRandomObject(Random random) {
		return REGISTRY.getRandomObject(random);
	}
}