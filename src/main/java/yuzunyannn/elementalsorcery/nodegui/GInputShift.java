package yuzunyannn.elementalsorcery.nodegui;

import java.util.ArrayList;
import java.util.List;

public class GInputShift {
	public int startIndex;
	public int endIndex;

	public int selectIndex;
	public List<String> alternative;

	public GInputShift() {
		alternative = new ArrayList<>();
	}

	public GInputShift(List<String> list) {
		alternative = list;
		selectIndex = list.size();
	}

}
