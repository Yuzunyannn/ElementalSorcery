package yuzunyannn.elementalsorcery.nodegui;

import java.util.ArrayList;
import java.util.List;

public abstract class GInputShift {
	public int startIndex;
	public int endIndex;

	public int selectIndex;

	public abstract int size();

	public abstract String get(int index);

	public boolean isEmpty() {
		return size() <= 0;
	}

	public static class Fast extends GInputShift {
		public String prefix;
		public List<String> alternative;
		public boolean notReverse = true;

		public Fast() {
			alternative = new ArrayList<>();
		}

		public Fast(List<String> list, boolean reverse) {
			alternative = list;
			selectIndex = list.size();
			notReverse = !reverse;
		}

		@Override
		public int size() {
			return alternative.size();
		}

		@Override
		public String get(int index) {
			String str = notReverse ? alternative.get(alternative.size() - index - 1) : alternative.get(index);
			if (this.prefix != null) str = this.prefix + str;
			return str;
		}
	}

}
