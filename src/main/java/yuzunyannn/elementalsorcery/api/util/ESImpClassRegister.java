package yuzunyannn.elementalsorcery.api.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.api.ESAPI;

public class ESImpClassRegister<T extends IForgeRegistryEntry<T>> {

	public static class EasyImp<T extends EasyImp<T>> implements IForgeRegistryEntry<T> {

		private ResourceLocation id;

		@Override
		public T setRegistryName(ResourceLocation name) {
			id = name;
			return (T) this;
		}

		@Override
		public ResourceLocation getRegistryName() {
			return id;
		}

		@Override
		public Class<T> getRegistryType() {
			return (Class<T>) getClass();
		}

	}

	protected final IntIdentityHashBiMap<Class<? extends T>> integerIdMap = new IntIdentityHashBiMap<>(256);
	protected final BiMap<ResourceLocation, Class<? extends T>> idMap = HashBiMap.create();
	private int nId = 0;

	public T newInstance(ResourceLocation id) {
		try {
			Class<? extends T> cls = getValue(id);
			if (cls == null) return null;
			return cls.newInstance().setRegistryName(id);
		} catch (Exception e) {
			ESAPI.logger.warn("实例化" + id + "时，出现异常！", e);
			return null;
		}
	}

	public T newInstance(int id) {
		return newInstance(getKey(id));
	}

	public <U extends T> U newInstance(Class<U> cls) {
		return (U) newInstance(getKey(cls));
	}

	public T newInstance(ResourceLocation id, Object... params) {
		try {
			Class<? extends T> cls = idMap.get(id);
			if (cls == null) return null;
			Class<?>[] clss = new Class<?>[params.length];
			for (int i = 0; i < clss.length; i++) clss[i] = params[i].getClass();
			Constructor<T> constructor = (Constructor<T>) cls.getConstructor(clss);
			return constructor.newInstance(params).setRegistryName(id);
		} catch (Exception e) {
			if (ESAPI.isDevelop) ESAPI.logger.warn("实例化" + id + "时，出现异常！", e);
			return null;
		}
	}

	public T newInstance(int id, Object... params) {
		return newInstance(getKey(id), params);
	}

	public T newInstance(ResourceLocation id, Class<?> types[], Object... params) {
		try {
			Class<? extends T> cls = idMap.get(id);
			if (cls == null) return null;
			Constructor<T> constructor = (Constructor<T>) cls.getConstructor(types);
			return constructor.newInstance(params).setRegistryName(id);
		} catch (InvocationTargetException e) {
			if (ESAPI.isDevelop) ESAPI.logger.warn("实例化" + id + "时，出现异常！", e.getCause());
			return null;
		} catch (Exception e) {
			if (ESAPI.isDevelop) ESAPI.logger.warn("实例化" + id + "时，出现异常！", e);
			return null;
		}
	}

	public T newInstance(int id, Class<?> types[], Object... params) {
		return newInstance(getKey(id), types, params);
	}

	public ResourceLocation getKey(Class<? extends T> cls) {
		BiMap<Class<? extends T>, ResourceLocation> rev = idMap.inverse();
		return rev.get(cls);
	}

	public ResourceLocation getKey(int id) {
		return getKey(getValue(id));
	}

	public int getId(Class<? extends T> cls) {
		return integerIdMap.getId(cls);
	}

	public Class<? extends T> getValue(ResourceLocation id) {
		return idMap.get(id);
	}

	public Class<? extends T> getValue(int id) {
		return integerIdMap.get(id);
	}

	public void register(int idx, ResourceLocation id, Class<? extends T> value) {
		idMap.put(id, value);
		integerIdMap.put(value, idx);
	}

	public void register(ResourceLocation id, Class<? extends T> value) {
		register(nId++, id, value);
	}

	public Set<ResourceLocation> keySet() {
		return idMap.keySet();
	}

	public Set<Class<? extends T>> valueSet() {
		return idMap.values();
	}

}
