package yuzunyan.elementalsorcery.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import yuzunyan.elementalsorcery.init.registries.TileItemRenderRegistries;

public class ESTileEntityItemStackRenderer extends TileEntityItemStackRenderer {
	private TileEntityItemStackRenderer parent;

	public ESTileEntityItemStackRenderer(TileEntityItemStackRenderer parent) {
		this.parent = parent;
	}

	@Override
	public void renderByItem(ItemStack itemStackIn) {
		this.renderByItem(itemStackIn, Minecraft.getMinecraft().getRenderPartialTicks());
	}

	@Override
	public void renderByItem(ItemStack itemStackIn, float partialTicks) {
		if (TileItemRenderRegistries.renderItIfPossible(itemStackIn, partialTicks))
			return;
		parent.renderByItem(itemStackIn);
	}
}
