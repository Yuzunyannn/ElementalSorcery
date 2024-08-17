package yuzunyannn.elementalsorcery.util.ds;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

public class PackList<T> implements List<T> {

	public static interface IArrayLike<T> {
		int size();

		T get(int index);
	}

	protected final IArrayLike<T> list;

	public PackList(Supplier<T>[] suppliers) {
		this(new IArrayLike<T>() {

			@Override
			public int size() {
				return suppliers.length;
			}

			@Override
			public T get(int index) {
				return suppliers[index].get();
			}
		});
	}

	public PackList(IArrayLike<T> list) {
		this.list = list;
	}

	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c) {
			if (!contains(obj)) return false;
		}
		return false;
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < list.size(); i++) if (list.get(i) == null) return i;
		} else {
			for (int i = 0; i < list.size(); i++) if (o.equals(list.get(i))) return i;
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return list.size() <= 0;
	}

	@Override
	public Iterator<T> iterator() {
		return listIterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = list.size(); i >= 0; i--) if (list.get(i) == null) return i;
		} else {
			for (int i = list.size(); i >= 0; i--) if (o.equals(list.get(i))) return i;
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

			@Override
			public void add(T e) {
				throw new UnsupportedOperationException("list is const");
			}

			@Override
			public boolean hasNext() {
				return _index < list.size();
			}

			@Override
			public boolean hasPrevious() {
				return _index < list.size() && _index - 1 > 0;
			}

			@Override
			public T next() {
				return list.get(++_index);
			}

			@Override
			public int nextIndex() {
				return _index + 1;
			}

			@Override
			public T previous() {
				return list.get(--_index);
			}

			@Override
			public int previousIndex() {
				return _index - 1;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("list is const");
			}

			@Override
			public void set(T e) {
				throw new UnsupportedOperationException("list is const");
			}
		};
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("list is const");
	}

	@Override
	public Object[] toArray() {
		Object[] objs = new Object[list.size()];
		for (int i = 0; i < list.size(); i++) objs[i] = list.get(i);
		return objs;
	}

	@Override
	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		int length = Math.min(a.length, list.size());
		for (int i = 0; i < length; i++) a[i] = (T) list.get(i);
		return a;
	}

}
