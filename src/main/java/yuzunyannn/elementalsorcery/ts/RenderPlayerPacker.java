package yuzunyannn.elementalsorcery.ts;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerPacker extends RenderPlayer {

	public final RenderPlayer parent;

	public RenderPlayerPacker(RenderPlayer other) {
		super(other.getRenderManager());
		parent = other;
	}

	@Override
	public ModelPlayer getMainModel() {
		return parent == null ? new ModelPlayer(1, false) : parent.getMainModel();
	}

	@Override
	public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		if (!entity.updateBlocked) PocketWatchClient.unbindGray();
		parent.doRender(entity, x, y, z, entityYaw, partialTicks);
		if (!entity.updateBlocked) PocketWatchClient.bindGray();
	}

	@Override
	public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
		return parent.getEntityTexture(entity);
	}

	@Override
	public void transformHeldFull3DItemLayer() {
		parent.transformHeldFull3DItemLayer();
	}

	@Override
	public void renderRightArm(AbstractClientPlayer clientPlayer) {
		parent.renderRightArm(clientPlayer);
	}

	@Override
	public void renderLeftArm(AbstractClientPlayer clientPlayer) {
		parent.renderLeftArm(clientPlayer);
	}

}
