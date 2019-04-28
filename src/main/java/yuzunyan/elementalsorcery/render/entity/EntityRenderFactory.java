package yuzunyan.elementalsorcery.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityRenderFactory<T extends Entity> implements IRenderFactory<T> {
	private final Class<? extends Render<T>> renderClass;

	public EntityRenderFactory(Class<? extends Render<T>> renderClass) {
		this.renderClass = renderClass;
	}

	@Override
	public Render<? super T> createRenderFor(RenderManager manager) {
		try {
			return renderClass.getConstructor(RenderManager.class).newInstance(manager);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
