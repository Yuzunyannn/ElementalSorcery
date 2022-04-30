package yuzunyannn.elementalsorcery.render.effect;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

public class EffectBatchSet extends EffectGroup {

	final private Set<EffectBatchType> batchs = new HashSet<>();

	public void add(EffectBatchType batchType, Effect effect) {
		batchType.effects.add(effect);
		batchs.add(batchType);
	}

	@Override
	public void add(Effect effect) {
		add(effect.typeBatch(), effect);
	}

	public void update() {
		Iterator<EffectBatchType> iter = batchs.iterator();
		while (iter.hasNext()) {
			EffectBatchType batch = iter.next();
			batch.effects.update();
			if (batch.effects.isEmpty()) iter.remove();
		}
	}

	public void render(float partialTicks) {
		if (batchs.isEmpty()) return;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		Iterator<EffectBatchType> batchIter = batchs.iterator();
		while (batchIter.hasNext()) {
			EffectBatchType batch = batchIter.next();
			batch.beginRender(tessellator, bufferbuilder);
			batch.effects.render(bufferbuilder, partialTicks);
			batch.endRender(tessellator, bufferbuilder);
		}
	}

	public void clear() {
		Iterator<EffectBatchType> iter = batchs.iterator();
		while (iter.hasNext()) iter.next().effects.clear();
		batchs.clear();
	}

	public boolean isEmpty() {
		return batchs.isEmpty();
	}
}
