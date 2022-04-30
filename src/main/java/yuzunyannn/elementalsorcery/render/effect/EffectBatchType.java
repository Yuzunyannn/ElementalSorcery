package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

public abstract class EffectBatchType {

	public final EffectList effects = new EffectList();

	public abstract void beginRender(Tessellator tessellator, BufferBuilder bufferbuilder);

	public abstract void endRender(Tessellator tessellator, BufferBuilder bufferbuilder);

}
