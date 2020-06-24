package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.client.model.ModelBase;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDDeconstructBox;
import yuzunyannn.elementalsorcery.tile.md.TileMDDeconstructBox;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderTileMDDeconstructBox extends RenderTileMDBase<TileMDDeconstructBox> {
	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_deconstruct_box.png");
	protected final ModelBase MODEL = new ModelMDDeconstructBox();

	@Override
	public void render(TileMDDeconstructBox tile, double x, double y, double z, float partialTicks, int destroyStage,
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
