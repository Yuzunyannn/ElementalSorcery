package yuzunyannn.elementalsorcery.render.effect.batch;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import yuzunyannn.elementalsorcery.render.effect.EffectBatchType;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class EffectBatchTypeNormal extends EffectBatchType {

	public final TextureBinder TEXTURE;

	public EffectBatchTypeNormal(TextureBinder res) {
		this.TEXTURE = res;
	}

	public EffectBatchTypeNormal(String path) {
		this(new TextureBinder(path));
	}

	public void bind() {
		TEXTURE.bind();
	}

	@Override
	public void beginRender(Tessellator tessellator, BufferBuilder bufferbuilder) {
		RenderHelper.disableLightmap(true);
		TEXTURE.bind();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	}

	@Override
	public void endRender(Tessellator tessellator, BufferBuilder bufferbuilder) {
		tessellator.draw();
		RenderHelper.disableLightmap(false);
	}

}
