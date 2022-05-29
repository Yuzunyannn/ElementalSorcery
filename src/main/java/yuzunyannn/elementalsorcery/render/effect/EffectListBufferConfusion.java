package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.ts.PocketWatchClient;
import yuzunyannn.elementalsorcery.util.render.Shaders;

public class EffectListBufferConfusion extends EffectListBuffer {

	@Override
	protected void renderCore(float partialTicks) {
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();

		RenderHelper.enableStandardItemLighting();
		// yuzunyannn.elementalsorcery.util.render.RenderHelper.disableLightmap(false);
		super.renderCore(partialTicks);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		// yuzunyannn.elementalsorcery.util.render.RenderHelper.disableLightmap(true);
	}

	@Override
	protected void renderFrame() {
		RenderHelper.disableStandardItemLighting();
		buffer.bindTexture();

		Shaders.ErrorCode.bind();
		if (!PocketWatchClient.isActive()) {
			Shaders.ErrorCode.setUniform("u_a", Effect.rand.nextDouble() * 0.0125);
			Shaders.ErrorCode.setUniform("u_n", EventClient.tickRender / 4 + 0.1);
		}
		buffer.bindFrame(false);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0, 0, 0).tex(0, 1).color(1, 1, 1, 1.0f).endVertex();
		bufferbuilder.pos(0, Effect.displayHeight, 0).tex(0, 0).color(1, 1, 1, 1.0f).endVertex();
		bufferbuilder.pos(Effect.displayWidth, Effect.displayHeight, 0).tex(1, 0).color(1, 1, 1, 1.0f).endVertex();
		bufferbuilder.pos(Effect.displayWidth, 0, 0).tex(1, 1).color(1, 1, 1, 1.0f).endVertex();
		tessellator.draw();
		buffer.unbindFrame();
		Shaders.ErrorCode.unbind();

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0, 0, 0).tex(0, 1).color(1, 1, 1, 1.0f).endVertex();
		bufferbuilder.pos(0, Effect.displayHeight, 0).tex(0, 0).color(1, 1, 1, 1.0f).endVertex();
		bufferbuilder.pos(Effect.displayWidth, Effect.displayHeight, 0).tex(1, 0).color(1, 1, 1, 1.0f).endVertex();
		bufferbuilder.pos(Effect.displayWidth, 0, 0).tex(1, 1).color(1, 1, 1, 1.0f).endVertex();
		tessellator.draw();

		RenderHelper.enableStandardItemLighting();
	}

}
