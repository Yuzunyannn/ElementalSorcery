package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelMDBase;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public abstract class RednerTileMDBase<T extends TileMDBase> extends TileEntitySpecialRenderer<T>
		implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_base.png");
	protected final ModelMDBase MODEL_BASE = new ModelMDBase();

	@Override
	public void render(TileMDBase tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL_BASE.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);
	}
}
