package yuzunyannn.elementalsorcery.parchment;

import java.util.List;

import net.minecraft.item.ItemStack;

public class PageTransformSimple extends PageTransform {
	private final String title;
	private final String value;
	private final int type;
	private final ItemStack origin;
	private final ItemStack output;
	private final ItemStack extra;
	private final List<ItemStack> list;

	public PageTransformSimple(String title, String value, ItemStack origin, ItemStack output, ItemStack extra,
			List<ItemStack> list, int guiId) {
		this.title = title;
		this.value = value;
		this.origin = origin;
		this.output = output;
		this.type = guiId;
		this.extra = extra;
		this.list = list;
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
		return "es.page." + title + ".title";
	}

	@Override
	public String getContext() {
		return "es.page." + value + ".describe";
	}
}
