package yuzunyannn.elementalsorcery.util.helper;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;

public class MultipleIterator<T> implements Iterator<T> {

	LinkedList<Iterator<T>> iters = new LinkedList<>();
	Iterator<T> iter;

	public void add(Iterator<T> iter) {
		iters.add(iter);
	}

	public void addRandom(Iterator<T> iter) {
		if (RandomHelper.rand.nextBoolean()) iters.addFirst(iter);
		else iters.addLast(iter);
	}

	public void addFirst(Iterator<T> iter) {
		iters.addFirst(iter);
	}

	public void addLast(Iterator<T> iter) {
		iters.addLast(iter);
	}

	@Override
	public boolean hasNext() {
		try {
			if (iter == null) {
				if (iters.isEmpty()) return false;
				iter = iters.pop();
			}
			if (!iter.hasNext()) {
				iter = null;
				return hasNext();
			}
			return true;
		} catch (ConcurrentModificationException e) {
			iter = null;
			return hasNext();
		}
	}

	@Override
	public T next() {
		return iter.next();
	}

	@Override
	public void remove() {
		iter.remove();
	}

}
