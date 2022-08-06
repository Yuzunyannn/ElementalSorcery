package yuzunyannn.elementalsorcery.api.util;

import java.lang.reflect.Constructor;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

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

	protected final BiMap<ResourceLocation, Class<? extends T>> REGISTRY = HashBiMap.create();

	/** 根据注册的id获取实例化 */
	public T newInstance(ResourceLocation id) {
		try {
			Class<? extends T> cls = REGISTRY.get(id);
			if (cls == null) return null;
			return cls.newInstance().setRegistryName(id);
		} catch (Exception e) {
			ESAPI.logger.warn("实例化" + id + "时，出现异常！", e);
			return null;
		}
	}

	public T newInstance(ResourceLocation id, Object... objs) {
		try {
			Class<? extends T> cls = REGISTRY.get(id);
			if (cls == null) return null;
			Class<?>[] clss = new Class<?>[objs.length];
			for (int i = 0; i < clss.length; i++) clss[i] = objs[i].getClass();
			Constructor<T> constructor = (Constructor<T>) cls.getConstructor(clss);
			return constructor.newInstance(objs).setRegistryName(id);
		} catch (Exception e) {
			ESAPI.logger.warn("实例化" + id + "时，出现异常！", e);
			return null;
		}
	}

	public <U extends T> U newInstance(Class<U> cls) {
		return (U) newInstance(this.getKey(cls));
	}

	public ResourceLocation getKey(Class<? extends T> cls) {
		BiMap<Class<? extends T>, ResourceLocation> rev = REGISTRY.inverse();
		return rev.get(cls);
	}

	public Class<? extends T> getValue(ResourceLocation id) {
		return REGISTRY.get(id);
	}

	public void register(ResourceLocation id, Class<? extends T> value) {
		REGISTRY.put(id, value);
	}

	public Set<ResourceLocation> keySet() {
		return REGISTRY.keySet();
	}

	public Set<Class<? extends T>> valueSet() {
		return REGISTRY.values();
	}

}
