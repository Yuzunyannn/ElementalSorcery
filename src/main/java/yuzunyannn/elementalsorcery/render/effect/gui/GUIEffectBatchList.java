package yuzunyannn.elementalsorcery.render.effect.gui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

public class GUIEffectBatchList<T extends GUIEffectBatch> {

	protected List<GUIEffectBatch> list = new LinkedList<>();

	public void add(T effect) {
		list.add(effect);
	}

	public void update() {
		Iterator<GUIEffectBatch> iter = list.iterator();
		while (iter.hasNext()) {
			GUIEffectBatch effect = iter.next();
			effect.update();
			if (effect.isDead()) iter.remove();
		}
	}

	public void render(float partialTicks) {
		if (list.isEmpty()) return;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GUIEffectBatch first = list.get(0);
		first.begin(tessellator, bufferbuilder);
		for (GUIEffectBatch obj : list) obj.render(partialTicks, bufferbuilder);
		first.end(tessellator, bufferbuilder);
	}

}
