package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.ModelSupremeTable;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeTable;

@SideOnly(Side.CLIENT)
public class RenderTileSupremeTable extends TileEntitySpecialRenderer<TileSupremeTable>
		implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/supreme_table.png");
	public static final ModelSupremeTable MODEL = new ModelSupremeTable();

	@Override
	public void render(TileSupremeTable tile, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.03125, alpha);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		float roate = tile.prevRoate + (tile.roate - tile.prevRoate) * partialTicks;
		float legR = tile.prevLegR + (tile.legR - tile.prevLegR) * partialTicks;
		MODEL.render(null, legR, roate, roate, 0, 0, 1.0f);
		GlStateManager.disableBlend();
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true, 0.0175, 0.0125, 0, 0);
	}
}
