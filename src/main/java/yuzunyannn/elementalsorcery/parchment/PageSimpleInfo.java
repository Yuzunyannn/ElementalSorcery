package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PageSimpleInfo extends PageSimple {

	private final String info;

	public PageSimpleInfo(String name, String info) {
		super(name);
		this.info = info;
	}

	public PageSimpleInfo(String name, String info, ItemStack icon) {
		super(name, icon);
		this.info = info;
	}

	public PageSimpleInfo(String name, String info, Item icon) {
		super(name, icon);
		this.info = info;
	}

	public PageSimpleInfo(String name, String info, Block icon) {
		super(name, icon);
		this.info = info;
	}

	public PageSimpleInfo(String name, String info, ItemStack icon, ItemStack background) {
		super(name, icon, background);
		this.info = info;
	}

	@Override
	public String getContext() {
		return super.getContext() + "." + info;
	}

}
