package yuzunyannn.elementalsorcery.render.effect;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.ds.DoubleKey;

@SideOnly(Side.CLIENT)
public class EffectEntityMapping {

	//-- use WeakValueHashMap
	@Deprecated
	public static class ValueWeakHashMap<K, V> {
		protected Map<K, WeakReference<V>> map = new HashMap();

		public void put(K key, V val) {
			map.put(key, new WeakReference<V>(val));
		}

		public boolean isEmpty() {
			return map.isEmpty();
		}

		public V get(K key) {
			WeakReference<V> ref = map.get(key);
			if (ref == null) return null;
			V obj = ref.get();
			if (obj == null) {
				refresh();
				return null;
			}
			return obj;
		}

		public void remove(K key) {
			map.remove(key);
		}

		public void refresh() {
			Iterator<Map.Entry<K, WeakReference<V>>> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<K, WeakReference<V>> entry = iter.next();
				if (entry.getValue().get() == null) iter.remove();
			}
		}

	}

	public final static ValueWeakHashMap<DoubleKey<UUID, String>, Effect> map = new ValueWeakHashMap();

	public static void setEffect(UUID uuid, String key, Effect effect) {
		if (effect.isDead()) return;
		map.put(DoubleKey.of(uuid, key), effect);
		map.refresh();
	}

	public static <T extends Effect> T getEffect(UUID uuid, String key, Class<T> cls) {
		Effect effect = map.get(DoubleKey.of(uuid, key));
		if (effect != null) {
			if (effect.isDead() || !cls.isAssignableFrom(effect.getClass())) {
				map.remove(DoubleKey.of(uuid, key));
				return null;
			}
			return (T) effect;
		}
		return null;
	}

	public static <T extends Effect> T getEffect(Entity entitiy, String key, Class<T> cls) {
		return getEffect(entitiy.getUniqueID(), key, cls);
	}

	public static void setEffect(Entity entitiy, String key, Effect effect) {
		setEffect(entitiy.getUniqueID(), key, effect);
	}

}
