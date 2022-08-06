package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDMagicSolidify;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicSolidify;

public class RenderTileMDMagicSolidify extends RenderTileMDBase<TileMDMagicSolidify> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_magic_solidify.png");
	protected static final ModelMDMagicSolidify MODEL = new ModelMDMagicSolidify();

	@Override
	public void render(TileMDMagicSolidify tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, false);
	}

}
