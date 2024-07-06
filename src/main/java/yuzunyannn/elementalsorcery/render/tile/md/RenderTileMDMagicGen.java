package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDMagicGen;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicGen;

public class RenderTileMDMagicGen extends RenderTileMDBase<TileMDMagicGen> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_magic_gen.png");
	public static final ModelMDMagicGen MODEL = new ModelMDMagicGen();

	@Override
	public void render(TileMDMagicGen tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		GlStateManager.disableCull();
		MODEL.render(null, tile.isFire() ? 1 : 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.enableCull();
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		GlStateManager.disableCull();
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, false);
		GlStateManager.enableCull();
	}

}
