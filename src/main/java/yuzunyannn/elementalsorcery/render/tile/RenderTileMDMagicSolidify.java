package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.render.model.ModelMDMagicSolidify;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicSolidify;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderTileMDMagicSolidify extends RenderTileMDBase<TileMDMagicSolidify> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_magic_solidify.png");
	protected final ModelMDMagicSolidify MODEL = new ModelMDMagicSolidify();

	@Override
	public void render(TileMDMagicSolidify tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		RenderHelper.render(stack, TEXTURE, MODEL, false);
	}

}
