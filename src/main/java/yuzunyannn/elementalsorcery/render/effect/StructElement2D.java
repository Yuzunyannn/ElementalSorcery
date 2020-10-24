package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public class StructElement2D {

	public float x, y;
	public float preX, preY;
	public float scale = 1;
	public float alpha = 1;
	public float preAlpha = alpha;
	public float r, g, b;
	public int lifeTime;
	public float drawSize = 0.5f;

	public void setColor(int c) {
		r = ((c >> 16) & 0xff) / 255f;
		g = ((c >> 8) & 0xff) / 255f;
		b = ((c >> 0) & 0xff) / 255f;
	}

	public void setColor(float r, float g, float b) {
		this.r = r;
		this.b = b;
		this.g = g;
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
		float x = RenderHelper.getPartialTicks(this.x, preX, partialTicks);
		float y = RenderHelper.getPartialTicks(this.y, preY, partialTicks);
		float alpha = RenderHelper.getPartialTicks(this.alpha, preAlpha, partialTicks);
		GlStateManager.color(r, g, b, alpha);
		GlStateManager.translate(x, y, -0.001f);
		GlStateManager.scale(scale, scale, scale);
		EffectElement.TEXTURE.bind();
		renderRect(bufferbuilder, drawSize);
		tessellator.draw();
		GlStateManager.popMatrix();
	}

	public static void renderRect(BufferBuilder bufferbuilder, float size) {
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-size, -size, 0).tex(0, 1).endVertex();
		bufferbuilder.pos(-size, size, 0).tex(0, 0).endVertex();
		bufferbuilder.pos(size, size, 0).tex(1, 0).endVertex();
		bufferbuilder.pos(size, -size, 0).tex(1, 1).endVertex();
	}
}
