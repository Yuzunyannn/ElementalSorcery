package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelItemStructureCraftCC;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftCC;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileItemStructureCraftCC extends TileEntitySpecialRenderer<TileItemStructureCraftCC>
		implements IRenderItem {

	static public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/is_craft_cc.png");
	static public final ModelItemStructureCraftCC MODEL = new ModelItemStructureCraftCC();

	@Override
	public void render(TileItemStructureCraftCC tile, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, EventClient.tickRender + partialTicks, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL, true);
	}

}
