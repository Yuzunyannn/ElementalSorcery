package yuzunyannn.elementalsorcery.util.item;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class ItemMatchResult {

	protected final boolean isSuccess;
	protected List<Map.Entry<ItemStack, Integer>> shrinks;

	static public ItemMatchResult fail() {
		return new ItemMatchResult(false);
	}

	public ItemMatchResult(boolean isSuccess) {
		this.isSuccess = isSuccess;
		if (isSuccess) shrinks = new LinkedList<>();
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void addToShink(ItemStack stack, int count) {
		if (shrinks == null) return;
		shrinks.add(new AbstractMap.SimpleEntry(stack, count));
	}

	public void doShrink() {
		if (shrinks == null) return;
		for (Map.Entry<ItemStack, Integer> entry : shrinks) {
			entry.getKey().shrink(entry.getValue());
		}
		shrinks = null;
	}

}
