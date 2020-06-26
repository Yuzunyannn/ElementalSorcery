package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelSupremeCraftingTable;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeCraftingTable;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileSupremeCraftingTable extends TileEntitySpecialRenderer<TileSupremeCraftingTable>
		implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/supreme_crafting_table.png");
	private final ModelSupremeCraftingTable MODEL = new ModelSupremeCraftingTable();

	@Override
	public void render(TileSupremeCraftingTable tile, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.03125, alpha);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		float roate = tile.prevRoate + (tile.roate - tile.prevRoate) * partialTicks;
		float legR = tile.prevLegR + (tile.legR - tile.prevLegR) * partialTicks;
		MODEL.render(null, legR, roate, roate, 0, 0, 1.0f);
		GlStateManager.disableBlend();
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL, false, 0.0175, 0.0125, 0, 0);
	}
}
