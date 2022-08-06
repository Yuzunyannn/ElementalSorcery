package yuzunyannn.elementalsorcery.render.effect.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElement;
import yuzunyannn.elementalsorcery.util.helper.Color;

public abstract class GUIEffectBatch {

	public float drawSize = 1f;

	public float x, y, prevX, prevY;
	public float scale = 1, prevScale = 1;
	public float alpha = 1, prevAlpha = alpha;

	public int lifeTime;

	public final Color color = new Color();

	public void setPosition(float x, float y) {
		this.prevX = this.x = x;
		this.prevY = this.y = y;
	}

	public void update() {
		prevX = x;
		prevY = y;
		prevScale = scale;
		prevAlpha = alpha;
	}

	public boolean isDead() {
		return this.lifeTime <= 0;
	}

	public void bindTexture() {
		EffectElement.BATCH_TYPE.bind();
	}

	public void begin(Tessellator tessellator, BufferBuilder bufferbuilder) {
		bindTexture();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	}

	public void end(Tessellator tessellator, BufferBuilder bufferbuilder) {
		tessellator.draw();
	}

	public void render(float partialTicks, BufferBuilder bufferbuilder) {
		float size = drawSize * RenderFriend.getPartialTicks(scale, prevScale, partialTicks);
		float a = RenderFriend.getPartialTicks(alpha, prevAlpha, partialTicks);
		float x = RenderFriend.getPartialTicks(this.x, this.prevX, partialTicks);
		float y = RenderFriend.getPartialTicks(this.y, this.prevY, partialTicks);
		bufferbuilder.pos(x - size, y - size, 0).tex(0, 1).color(color.r, color.g, color.b, a).endVertex();
		bufferbuilder.pos(x - size, y + size, 0).tex(0, 0).color(color.r, color.g, color.b, a).endVertex();
		bufferbuilder.pos(x + size, y + size, 0).tex(1, 0).color(color.r, color.g, color.b, a).endVertex();
		bufferbuilder.pos(x + size, y - size, 0).tex(1, 1).color(color.r, color.g, color.b, a).endVertex();
	}
}
