package yuzunyannn.elementalsorcery.ts;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPacker<T extends Entity> extends Render<T> {

	final public Render<T> parent;

	public RenderPacker(Render<T> other) {
		super(PocketWatchClient.mc.getRenderManager());
		this.parent = other;
	}

	@Override
	protected ResourceLocation getEntityTexture(T entity) {
		return null;
	}

	@Override
	public void setRenderOutlines(boolean renderOutlinesIn) {
		parent.setRenderOutlines(renderOutlinesIn);
	}

	@Override
	public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
		return parent.shouldRender(livingEntity, camera, camX, camY, camZ);
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (!entity.updateBlocked) PocketWatchClient.unbindGray();
		parent.doRender(entity, x, y, z, entityYaw, 0);
		if (!entity.updateBlocked) PocketWatchClient.bindGray();
	}

	@Override
	public void bindTexture(ResourceLocation location) {
		parent.bindTexture(location);
	}

	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		parent.doRenderShadowAndFire(entityIn, x, y, z, yaw, 0);
	}

	@Override
	public FontRenderer getFontRendererFromRenderManager() {
		return parent.getFontRendererFromRenderManager();
	}

	@Override
	public RenderManager getRenderManager() {
		return parent.getRenderManager();
	}

	@Override
	public boolean isMultipass() {
		return parent.isMultipass();
	}

	@Override
	public void renderMultipass(T p_188300_1_, double p_188300_2_, double p_188300_4_, double p_188300_6_,
			float p_188300_8_, float p_188300_9_) {
		parent.renderMultipass(p_188300_1_, p_188300_2_, p_188300_4_, p_188300_6_, p_188300_8_, p_188300_9_);
	}

}
