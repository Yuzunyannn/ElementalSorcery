package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelMeltCauldron;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileMeltCauldron extends TileEntitySpecialRenderer<TileMeltCauldron> implements IRenderItem {

	static final public TextureBinder TEXTURE = new TextureBinder("textures/blocks/melt_cauldron.png");
	static final public TextureBinder TEXTURE_FLUID = new TextureBinder("textures/blocks/fluids/magic_melt.png");
	static final public TextureBinder TEXTURE_FLUID_BLOCK = new TextureBinder(
			"textures/blocks/fluids/magic_melt_block.png");

	private final ModelMeltCauldron MODEL = new ModelMeltCauldron();

	@Override
	public void render(TileMeltCauldron tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha0) {

		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha0);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		if (tile.getVolume() > 0) {
			tile.isRendered = true;
			float volume = RenderHelper.getPartialTicks(tile.getVolume(), tile.prevVolume, partialTicks);
			GlStateManager.pushMatrix();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.translate(x, y + 0.125f + volume / 1000.0f * 0.68f, z);
			float T = (int) tile.getTemperature();
			if (T < TileMeltCauldron.START_TEMPERATURE) {
				GlStateManager.enableBlend();
				float alpha = T / TileMeltCauldron.START_TEMPERATURE;
				// 画流体
				GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
				int at = EventClient.tickRender % 32;
				TEXTURE_FLUID.bind();
				this.drawVolume(at * 0.03125, 0.03125);
				// 画方块
				tile.bindDynamicTexture();
				GlStateManager.enableBlend();
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f - alpha);
				this.drawVolume(0, 1);
				GlStateManager.disableBlend();
			} else {
				int at = EventClient.tick % 32;
				TEXTURE_FLUID.bind();
				this.drawVolume(at * 0.03125, 0.03125);
			}
			GlStateManager.popMatrix();
		}
	}

	public void drawVolume(double frameAt, final double frameRate) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(0.0625, 0, 0.0625).tex(0, frameAt).endVertex();
		bufferbuilder.pos(0.0625, 0, 0.9375).tex(0, frameAt + frameRate).endVertex();
		bufferbuilder.pos(0.9375, 0, 0.9375).tex(1, frameAt + frameRate).endVertex();
		bufferbuilder.pos(0.9375, 0, 0.0625).tex(1, frameAt).endVertex();
		tessellator.draw();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL, true);
	}
}
