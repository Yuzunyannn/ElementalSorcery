package yuzunyannn.elementalsorcery.render.effect.batch;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;

public class EffectBatchTypeGlow extends EffectBatchTypeNormal {

	public EffectBatchTypeGlow(TextureBinder res) {
		super(res);
	}

	public EffectBatchTypeGlow(String path) {
		super(path);
	}

	@Override
	public void beginRender(Tessellator tessellator, BufferBuilder bufferbuilder) {
		GlStateManager.depthFunc(519);
		super.beginRender(tessellator, bufferbuilder);
	}

	@Override
	public void endRender(Tessellator tessellator, BufferBuilder bufferbuilder) {
		super.endRender(tessellator, bufferbuilder);
		GlStateManager.depthFunc(515);
	}

}
