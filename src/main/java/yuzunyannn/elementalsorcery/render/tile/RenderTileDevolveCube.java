package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.ModelDevolveCube;
import yuzunyannn.elementalsorcery.tile.altar.TileDevolveCube;

@SideOnly(Side.CLIENT)
public class RenderTileDevolveCube extends TileEntitySpecialRenderer<TileDevolveCube> implements IRenderItem {

	public final static TextureBinder TEXTURE = new TextureBinder("textures/blocks/devolve_cube.png");
	public final static ModelDevolveCube MODEL = new ModelDevolveCube();

	@Override
	public void render(TileDevolveCube tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		float rand = tile.getPos().hashCode();
		float renderTick = RenderFriend.getPartialTicks(tile.renderTick, tile.prevRenderTick, partialTicks);
		MODEL.render(null, 0, 0, rand + renderTick, 0, 0, 1.0f);
		GlStateManager.disableBlend();
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, false);
	}

}
