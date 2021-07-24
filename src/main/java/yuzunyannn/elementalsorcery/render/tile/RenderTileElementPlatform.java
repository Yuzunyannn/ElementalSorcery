package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelElementPlatform;
import yuzunyannn.elementalsorcery.tile.TileElementPlatform;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileElementPlatform extends TileEntitySpecialRenderer<TileElementPlatform> implements IRenderItem {

	static public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/element_platform.png");
	static public final ModelElementPlatform MODEL = new ModelElementPlatform();

	public RenderTileElementPlatform() {

	}

	@Override
	public void render(TileElementPlatform tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);

		MODEL.render(null, 0, 0, EventClient.getGlobalRotateInRender(partialTicks), 0, 0, 1.0f);

		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		ItemStack stack = tile.getStack();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.55, z + 0.5);
		yuzunyannn.elementalsorcery.util.render.RenderHelper.layItemPositionFix(stack);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL, false, 0.038, 0.0175, 0.1, 0);
	}

}