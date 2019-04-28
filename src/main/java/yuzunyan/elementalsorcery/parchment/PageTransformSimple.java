package yuzunyan.elementalsorcery.parchment;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PageTransformSimple extends PageTransform {

	private final String name;

	public PageTransformSimple(String name, ItemStack origin, ItemStack output, int guiId) {
		super(origin, output, guiId);
		this.name = name;
	}

	public PageTransformSimple(String name, Item origin, Item output, int guiId) {
		super(origin, output, guiId);
		this.name = name;
	}

	public PageTransformSimple(String name, Block origin, Item output, Item extra, int guiId) {
		super(Item.getItemFromBlock(origin), output, extra, guiId);
		this.name = name;
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
