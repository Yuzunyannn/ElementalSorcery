package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelLantern;
import yuzunyannn.elementalsorcery.tile.TileLantern;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;
@SideOnly(Side.CLIENT)
public class RenderTileLantern extends TileEntitySpecialRenderer<TileLantern> implements IRenderItem {

	private TextureBinder TEXTURE = new TextureBinder("textures/blocks/lantern.png");
	private final ModelLantern MODEL = new ModelLantern();

	@Override
	public void render(TileLantern tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		if (RenderHelper.bindDestoryTexture(destroyStage, rendererDispatcher, DESTROY_STAGES))
			TEXTURE.bind();
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + 0.5, y +  0.0625, z + 0.5);
		GlStateManager.scale(0.0625, 0.0625, 0.0625);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();
		RenderHelper.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL, false, 0.0375, 0.0175, 0.05, 0);
	}

}
