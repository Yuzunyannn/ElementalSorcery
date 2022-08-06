package yuzunyannn.elementalsorcery.api.util;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class ItemMatchResult {

	protected final boolean isSuccess;
	protected List<Runnable> shrinks;

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
		addToShink(() -> {
			stack.shrink(count);
		});
	}

	public void addToShink(Runnable func) {
		if (shrinks == null) return;
		shrinks.add(func);
	}

	public void doShrink() {
		if (shrinks == null) return;
		for (Runnable func : shrinks) func.run();
		shrinks = null;
	}

}
