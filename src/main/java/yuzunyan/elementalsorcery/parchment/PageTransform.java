package yuzunyan.elementalsorcery.parchment;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PageTransform extends Page {

	static int INFUSION = 1;
	static int SEPARATE = 2;
	static int SPELLALTAR = 3;

	protected ItemStack origin = ItemStack.EMPTY;
	protected ItemStack ouput = ItemStack.EMPTY;
	protected ItemStack extra = ItemStack.EMPTY;
	protected List<ItemStack> list = null;;
	protected int guiId;

	public PageTransform(ItemStack origin, ItemStack output, int guiId) {
		this.origin = origin;
		this.ouput = output;
		this.guiId = guiId;
	}

	public PageTransform(ItemStack origin, ItemStack output, ItemStack extra, int guiId) {
		this.origin = origin;
		this.ouput = output;
		this.extra = extra;
		this.guiId = guiId;
	}

	public PageTransform(ItemStack origin, ItemStack output, List<ItemStack> list, int guiId) {
		this.origin = origin;
		this.ouput = output;
		this.list = list;
		this.guiId = guiId;
	}

	public PageTransform(Item origin, Item output, int guiId) {
		this(new ItemStack(origin), new ItemStack(output), guiId);
	}

	public PageTransform(Item origin, Item output, Item extra, int guiId) {
		this(new ItemStack(origin), new ItemStack(output), new ItemStack(extra), guiId);
	}

	public PageTransform(Item origin, Item output, List<ItemStack> list, int guiId) {
		this(new ItemStack(origin), new ItemStack(output), list, guiId);
	}

	public int getTransformGui() {
		return guiId;
	}

	@Override
	public ItemStack getOrigin() {
		return origin;
	}

	@Override
	public ItemStack getOutput() {
		return ouput;
	}

	@Override
	public ItemStack getExtra() {
		return extra;
	}

	@Override
	public List<ItemStack> getItemList() {
		return this.list;
	}
}
