package yuzunyannn.elementalsorcery.render.effect.gui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElement;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class StructElement2D {

	public float x, y;
	public float preX, preY;
	public float scale = 1;
	public float alpha = 1;
	public float preAlpha = alpha;
	public int lifeTime;
	public float drawSize = 0.5f;
	public final Color color = new Color();

	public void setColor(int c) {
		setColor(((c >> 16) & 0xff) / 255f, ((c >> 8) & 0xff) / 255f, ((c >> 0) & 0xff) / 255f);
	}

	public void setColor(float r, float g, float b) {
		color.setColor(r, g, b);
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void updatePrev() {
		preX = x;
		preY = y;
		preAlpha = alpha;
	}

	public void render(float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		this.render(partialTicks, bufferbuilder, tessellator);
	}

	public void render(float partialTicks, BufferBuilder bufferbuilder, Tessellator tessellator) {
		GlStateManager.pushMatrix();
		float x = RenderFriend.getPartialTicks(this.x, preX, partialTicks);
		float y = RenderFriend.getPartialTicks(this.y, preY, partialTicks);
		float alpha = RenderFriend.getPartialTicks(this.alpha, preAlpha, partialTicks);
		GlStateManager.color(color.r, color.g, color.b, alpha);
		GlStateManager.translate(x, y, -0.001f);
		GlStateManager.scale(scale, scale, scale);
		bindTexture();
		renderRect(bufferbuilder, drawSize);
		tessellator.draw();
		GlStateManager.popMatrix();
	}

	public void bindTexture() {
		EffectElement.BATCH_TYPE.bind();
	}

	public static void renderRect(BufferBuilder bufferbuilder, float size) {
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-size, -size, 0).tex(0, 1).endVertex();
		bufferbuilder.pos(-size, size, 0).tex(0, 0).endVertex();
		bufferbuilder.pos(size, size, 0).tex(1, 0).endVertex();
		bufferbuilder.pos(size, -size, 0).tex(1, 1).endVertex();
	}
}
