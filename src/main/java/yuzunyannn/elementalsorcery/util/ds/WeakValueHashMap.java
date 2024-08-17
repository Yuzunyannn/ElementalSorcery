package yuzunyannn.elementalsorcery.util.ds;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class WeakValueHashMap<K, V> extends EntryReplaceMap<K, V, WeakValueHashMap.WeakValueEntry<K, V>> {

	@SuppressWarnings("unused")
	static class WeakValueEntry<K, V> extends WeakReference<V> implements Map.Entry<K, V> {
		private final K key;

		WeakValueEntry(K key, V value, ReferenceQueue<? super V> queue) {
			super(value, queue);
			this.key = key;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return get();
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o instanceof WeakValueEntry) {
				WeakValueEntry<?, ?> other = (WeakValueEntry<?, ?>) o;
				return key.equals(other.key);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}

		@Override
		public String toString() {
			return String.valueOf(get());
		}
	}

	private final ReferenceQueue<V> queue = new ReferenceQueue<>();

	@Override
	protected WeakValueEntry<K, V> entry(K k, V v) {
		return new WeakValueEntry(k, v, queue);
	}

	@Override
	public V put(K key, V value) {
		this.cleanup();
		return super.put(key, value);
	}

	@Override
	public int size() {
		this.clear();
		return super.size();
	}

	@Override
	public boolean isEmpty() {
		this.clear();
		return super.isEmpty();
	}

	@Override
	public Collection<V> values() {
		this.cleanup();
		return super.values();
	}

	@Override
	public Set<K> keySet() {
		this.cleanup();
		return super.keySet();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		this.cleanup();
		return super.entrySet();
	}

	@Override
	public boolean containsKey(Object key) {
		this.cleanup();
		return super.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		this.cleanup();
		return super.containsValue(value);
	}

	private void cleanup() {
		WeakValueEntry<K, V> ref;
		while ((ref = (WeakValueEntry<K, V>) queue.poll()) != null) super.remove(ref.key);
	}

}
