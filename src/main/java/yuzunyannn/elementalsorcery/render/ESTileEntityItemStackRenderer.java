package yuzunyannn.elementalsorcery.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
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