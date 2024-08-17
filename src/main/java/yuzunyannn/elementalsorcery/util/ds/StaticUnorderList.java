package yuzunyannn.elementalsorcery.util.ds;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StaticUnorderList<T> implements List<T> {

	private int usedSize;
	private final T[] objs;
	private int magic;

	public StaticUnorderList(int size) {
		objs = (T[]) new Object[size];
	}

	@Override
	public boolean add(T e) {
		if (usedSize >= objs.length) return false;
		objs[usedSize++] = e;
		magic++;
		return true;
	}

	@Override
	public void add(int index, T element) {
		if (usedSize >= objs.length) throw new ArrayIndexOutOfBoundsException();
		if (index >= usedSize) throw new ArrayIndexOutOfBoundsException();
		objs[usedSize++] = objs[index];
		objs[index] = element;
		magic++;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (T obj : c) add(obj);
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException("addAll(int, Collection) is not supported");
	}

	@Override
	public void clear() {
		usedSize = 0;
		magic++;
	}

	@Override
	public boolean contains(Object o) {
		if (o == null) {
			for (int i = 0; i < usedSize; i++) {
				if (objs[i] == null) return true;
			}
		} else {
			for (int i = 0; i < usedSize; i++) {
				if (o.equals(objs[i])) return true;
			}
		}
		return false;

	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c) {
			if (!contains(obj)) return false;
		}
		return true;
	}

	@Override
	public T get(int index) {
		return objs[index];
	}

	@Override
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < usedSize; i++) if (objs[i] == null) return i;
		} else {
			for (int i = 0; i < usedSize; i++) if (o.equals(objs[i])) return i;
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return usedSize <= 0;
	}

	public boolean isFull() {
		return usedSize >= objs.length;
	}

	@Override
	public Iterator<T> iterator() {
		return listIterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = usedSize; i >= 0; i--) if (objs[i] == null) return i;
		} else {
			for (int i = usedSize; i >= 0; i--) if (o.equals(objs[i])) return i;
		}
		return -1;
	}

	@Override
	public ListIterator<T> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new ListIterator<T>() {

			int _index = index - 1;
			int _magic = magic;

			@Override
			public void add(T e) {
				throw new UnsupportedOperationException("addAll(int, Collection) is not supported");
			}

			@Override
			public boolean hasNext() {
				if (_magic != magic) throw new ConcurrentModificationException();
				return _index + 1 < usedSize;
			}

			@Override
			public boolean hasPrevious() {
				if (_magic != magic) throw new ConcurrentModificationException();
				return _index - 1 > 0 && _index < usedSize;
			}

			@Override
			public T next() {
				return objs[++_index];
			}

			@Override
			public int nextIndex() {
				return _index + 1;
			}

			@Override
			public T previous() {
				return objs[--_index];
			}

			@Override
			public int previousIndex() {
				return _index - 1;
			}

			@Override
			public void remove() {
				if (_magic != magic) throw new ConcurrentModificationException();
				StaticUnorderList.this.remove(_index);
				_index = _index - 1;
				_magic = magic;
			}

			@Override
			public void set(T e) {
				StaticUnorderList.this.set(_index, e);
				_magic = magic;
			}
		};
	}

	@Override
	public boolean remove(Object o) {
		int i = indexOf(o);
		if (i == -1) return false;
		return remove(i) != null;
	}

	@Override
	public T remove(int index) {
		if (index >= usedSize) return null;
		T obj = objs[index];
		objs[index] = objs[--usedSize];
		objs[usedSize] = null;
		magic++;
		return obj;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object obj : c) remove(obj);
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("retainAll(Collection) is not supported");
	}

	@Override
	public T set(int index, T element) {
		if (index >= usedSize) return null;
		T obj = objs[index];
		objs[index] = element;
		return obj;
	}

	@Override
	public int size() {
		return usedSize;
	}

	public int capacity() {
		return objs.length;
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("retainAll(Collection) is not supported");
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException("retainAll(Collection) is not supported");
	}

	@Override
	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		int l = Math.min(a.length, usedSize);
		for (int i = 0; i < l; i++) a[i] = (T) objs[i];
		return a;
	}

	public T[] getArray() {
		return objs;
	}

}
