package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.render.model.ModelMDMagicGen;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicGen;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderTileMDMagicGen extends RenderTileMDBase<TileMDMagicGen> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_magic_gen.png");
	protected final ModelMDMagicGen MODEL = new ModelMDMagicGen();

	@Override
	public void render(TileMDMagicGen tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		GlStateManager.disableCull();
		MODEL.render(null, tile.isFire() ? 1 : 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.enableCull();
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		GlStateManager.disableCull();
		RenderHelper.render(stack, TEXTURE, MODEL, false);
		GlStateManager.enableCull();
	}

}
