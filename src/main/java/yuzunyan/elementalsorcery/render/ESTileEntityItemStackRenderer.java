package yuzunyan.elementalsorcery.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class ESTileEntityItemStackRenderer extends TileEntityItemStackRenderer {

	final IRenderItem itemRender;

	public ESTileEntityItemStackRenderer(IRenderItem itemRender) {
		this.itemRender = itemRender;
	}

	@Override
	public void renderByItem(ItemStack itemStackIn) {
		this.renderByItem(itemStackIn, Minecraft.getMinecraft().getRenderPartialTicks());
	}

	@Override
	public void renderByItem(ItemStack itemStackIn, float partialTicks) {
		itemRender.render(itemStackIn, partialTicks);
	}
}
