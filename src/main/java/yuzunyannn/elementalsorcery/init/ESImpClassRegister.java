package yuzunyannn.elementalsorcery.init;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class ESImpClassRegister<T extends IForgeRegistryEntry<T>> {

	public static class EasyImp<T extends IForgeRegistryEntry<T>> implements IForgeRegistryEntry<T> {

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
			return cls.newInstance().setRegistryName(id);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("实例化" + id + "时，出现异常！", e);
			return null;
		}
	}

	public T newInstance(String id) {
		return newInstance(TextHelper.toESResourceLocation(id));
	}

	public <U extends T> U newInstance(Class<U> cls) {
		return (U) newInstance(this.getKey(cls));
	}

	public ResourceLocation getKey(Class<? extends T> cls) {
		BiMap<Class<? extends T>, ResourceLocation> rev = REGISTRY.inverse();
		return rev.get(cls);
	}

	public void register(ResourceLocation id, Class<? extends T> value) {
		REGISTRY.put(id, value);
	}

}
