package yuzunyannn.elementalsorcery.render.effect;

import java.util.ArrayDeque;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

public abstract class EffectBatchType {

	public final ArrayDeque<Effect> effects = new ArrayDeque<>();

	public abstract void beginRender(Tessellator tessellator, BufferBuilder bufferbuilder);

	public abstract void endRender(Tessellator tessellator, BufferBuilder bufferbuilder);

}
