package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
	private final ModelMeltCauldron MODEL = new ModelMeltCauldron();

	@Override
	public void render(TileMeltCauldron tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha0) {

		if (RenderHelper.bindDestoryTexture(destroyStage, rendererDispatcher, DESTROY_STAGES))
			TEXTURE.bind();
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + 0.5, y, z + 0.5);
		GlStateManager.scale(0.0625, 0.0625, 0.0625);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		int volume = tile.getVolume();
		if (volume > 0) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 0.125f + volume / 1000.0f * 0.68f, z);
			float T = (int) tile.getTemperature();
			if (T < tile.START_TEMPERATURE) {
				GlStateManager.enableBlend();
				float alpha = T / tile.START_TEMPERATURE;
				// 画流体
				GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
				int at = EventClient.tick % 32;
				TEXTURE_FLUID.bind();
				this.drawVolume(at * 0.03125, 0.03125);
				// 画方块
				ResourceLocation tex = tile.getResultTexture();
				if (tex != null) {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f - alpha);
					this.bindTexture(tex);
					this.drawVolume(0, 1);
				}
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
