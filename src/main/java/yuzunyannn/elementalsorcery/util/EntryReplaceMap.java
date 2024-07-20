package yuzunyannn.elementalsorcery.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class EntryReplaceMap<K, V, E extends Entry<K, V>> implements Map<K, V> {
	private final Map<K, E> internal = new HashMap<>();

	protected abstract E entry(K k, V v);

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return internal.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for (E entry : internal.values()) {
			if (entry.getValue().equals(value)) return true;
		}
		return false;
	}

	@Override
	public V get(Object key) {
		E entry = internal.get(key);
		return entry == null ? null : entry.getValue();
	}

	@Override
	public V put(K key, V value) {
		E entry = entry(key, value);
		E oldEntry = internal.put(key, entry);
		return oldEntry == null ? null : oldEntry.getValue();
	}

	@Override
	public V remove(Object key) {
		E entry = internal.remove(key);
		return entry == null ? null : entry.getValue();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) put(entry.getKey(), entry.getValue());
	}

	@Override
	public void clear() {
		internal.clear();
	}

	@Override
	public Set<K> keySet() {
		return internal.keySet();
	}

	@Override
	public Collection<V> values() {
		return new Collection<V>() {
			@Override
			public int size() {
				return internal.size();
			}

			@Override
			public boolean isEmpty() {
				return internal.isEmpty();
			}

			@Override
			public boolean contains(Object o) {
				return EntryReplaceMap.this.containsValue(o);
			}

			@Override
			public java.util.Iterator<V> iterator() {
				return new java.util.Iterator<V>() {
					private final java.util.Iterator<E> iterator = internal.values().iterator();

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public V next() {
						return iterator.next().getValue();
					}
				};
			}

			@Override
			public Object[] toArray() {
				return internal.values().stream().map(E::getValue).toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean add(V v) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean remove(Object o) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				for (Object e : c) {
					if (!contains(e)) { return false; }
				}
				return true;
			}

			@Override
			public boolean addAll(Collection<? extends V> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void clear() {
				EntryReplaceMap.this.clear();
			}
		};
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new Set<Map.Entry<K, V>>() {
			@Override
			public int size() {
				return internal.size();
			}

			@Override
			public boolean isEmpty() {
				return internal.isEmpty();
			}

			@Override
			public boolean contains(Object o) {
				if (!(o instanceof Map.Entry)) { return false; }
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
				E myEntry = internal.get(entry.getKey());
				return myEntry != null && myEntry.equals(entry);
			}

			@Override
			public java.util.Iterator<Map.Entry<K, V>> iterator() {
				return new java.util.Iterator<Map.Entry<K, V>>() {
					private final java.util.Iterator<E> iterator = internal.values().iterator();

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public Map.Entry<K, V> next() {
						return iterator.next();
					}
				};
			}

			@Override
			public Object[] toArray() {
				return internal.values().toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				return internal.values().toArray(a);
			}

			@Override
			public boolean add(Map.Entry<K, V> kvEntry) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean remove(Object o) {
				if (!(o instanceof Map.Entry)) { return false; }
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
				return internal.remove(entry.getKey()) != null;
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				for (Object e : c) {
					if (!contains(e)) { return false; }
				}
				return true;
			}

			@Override
			public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void clear() {
				EntryReplaceMap.this.clear();
			}
		};
	}
	
	@Override
	public String toString() {
		return internal.toString();
	}
}
