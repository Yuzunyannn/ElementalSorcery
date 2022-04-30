package yuzunyannn.elementalsorcery.render.effect;

import java.util.ArrayDeque;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class EffectList extends EffectGroup {

	final protected ArrayDeque<Effect> effects = new ArrayDeque<>();

	public void add(Effect effect) {
		effects.add(effect);
	}

	public void update() {
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

	public void render(float partialTicks) {
		Iterator<Effect> iter = effects.iterator();
		while (iter.hasNext()) {
			try {
				iter.next().doRender(partialTicks);
			} catch (Exception e) {
				iter.remove();
				ElementalSorcery.logger.warn("Effect Render Error", e);
			}
		}
	}

	public void render(BufferBuilder bufferbuilder, float partialTicks) {
		Iterator<Effect> iter = effects.iterator();
		while (iter.hasNext()) {
			try {
				iter.next().doRender(bufferbuilder, partialTicks);
			} catch (Exception e) {
				iter.remove();
				ElementalSorcery.logger.warn("Effect Batch Render Error", e);
			}
		}
	}

	public void clear() {
		effects.clear();
	}

	public boolean isEmpty() {
		return effects.isEmpty();
	}
}
