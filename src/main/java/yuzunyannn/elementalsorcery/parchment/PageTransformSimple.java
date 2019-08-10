package yuzunyannn.elementalsorcery.parchment;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PageTransformSimple extends PageTransform {
	private final String name;
	private final int type;
	private final ItemStack origin;
	private final ItemStack output;
	private final ItemStack extra;
	private final List<ItemStack> list;

	public PageTransformSimple(String name, ItemStack origin, ItemStack output, ItemStack extra, List<ItemStack> list,
			int guiId) {
		this.name = name;
		this.origin = origin;
		this.output = output;
		this.type = guiId;
		this.extra = extra;
		this.list = list;
	}

	public PageTransformSimple(String name, Item origin, Item output, int guiId) {
		this(name, new ItemStack(origin), new ItemStack(output), ItemStack.EMPTY, null, guiId);
	}

	public PageTransformSimple(String name, Item origin, Item output, List<ItemStack> list, int guiId) {
		this(name, new ItemStack(origin), new ItemStack(output), ItemStack.EMPTY, list, guiId);
	}

	@Override
	protected ItemStack getOrigin() {
		return this.origin;
	}

	@Override
	protected ItemStack getOutput() {
		return this.output;
	}

	@Override
	protected ItemStack getExtra() {
		return this.extra;
	}

	@Override
	protected List<ItemStack> getItemList() {
		return this.list;
	}

	@Override
	protected int getType() {
		return this.type;
	}

	@Override
	public String getTitle() {
		return "page." + name;
	}

	@Override
	public String getContext() {
		return "page." + name + ".ct";
	}
}
