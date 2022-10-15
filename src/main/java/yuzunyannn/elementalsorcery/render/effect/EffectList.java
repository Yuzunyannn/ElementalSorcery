package yuzunyannn.elementalsorcery.render.effect;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;

public class EffectList extends EffectGroup {

	final protected ArrayDeque<Effect> effects = new ArrayDeque<>();
	final protected Set<EffectBatchType> batchs = new HashSet<>();

	public void add(Effect effect) {
		if (effect.typeBatch() != null) add(effect.typeBatch(), effect);
		else effects.add(effect);
	}

	public void add(EffectBatchType batchType, Effect effect) {
		batchType.effects.add(effect);
		batchs.add(batchType);
	}

	protected void update(ArrayDeque<Effect> effects) {
		Iterator<Effect> iter = effects.iterator();
		World world = Minecraft.getMinecraft().world;
		while (iter.hasNext()) {
			Effect effect = iter.next();
			if (effect.isDead() || world != effect.world) {
				iter.remove();
				continue;
			}
			effect.onUpdate();
		}
	}

	public void update() {
		update(effects);
		Iterator<EffectBatchType> iter = batchs.iterator();
		while (iter.hasNext()) {
			EffectBatchType batch = iter.next();
			update(batch.effects);
			if (batch.effects.isEmpty()) iter.remove();
		}

	}

	public void render(float partialTicks) {
		if (!effects.isEmpty()) {
			Iterator<Effect> iter = effects.iterator();
			while (iter.hasNext()) {
				try {
					iter.next().doRender(partialTicks);
				} catch (Exception e) {
					iter.remove();
					ESAPI.logger.warn("Effect Render Error", e);
				}
			}
		}
		if (!batchs.isEmpty()) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			Iterator<EffectBatchType> batchIter = batchs.iterator();
			while (batchIter.hasNext()) {
				EffectBatchType batch = batchIter.next();
				batch.beginRender(tessellator, bufferbuilder);
				Iterator<Effect> iter = batch.effects.iterator();
				while (iter.hasNext()) {
					try {
						iter.next().doRender(bufferbuilder, partialTicks);
					} catch (Exception e) {
						iter.remove();
						ESAPI.logger.warn("Effect Batch Render Error", e);
					}
				}
				batch.endRender(tessellator, bufferbuilder);
			}
		}
	}

	public void clear() {
		effects.clear();
		Iterator<EffectBatchType> iter = batchs.iterator();
		while (iter.hasNext()) iter.next().effects.clear();
		batchs.clear();
	}

	public boolean isEmpty() {
		return effects.isEmpty() && batchs.isEmpty();
	}

	@Override
	public Iterator<Effect> iterator() {
		return new MyIterator();
	}

	class MyIterator implements Iterator<Effect> {

		Iterator<Effect> effectIter;
		Iterator<EffectBatchType> batchIter;

		@Override
		public boolean hasNext() {
			if (effectIter == null) effectIter = effects.iterator();
			boolean hasNext = effectIter.hasNext();
			if (hasNext) return true;
			if (batchIter == null) batchIter = batchs.iterator();
			if (batchIter.hasNext()) {
				effectIter = batchIter.next().effects.iterator();
				return this.hasNext();
			} else {
				effectIter = null;
				effectIter = null;
				return false;
			}
		}

		@Override
		public Effect next() {
			return effectIter.next();
		}

		@Override
		public void remove() {
			effectIter.remove();
		}

	}
}
