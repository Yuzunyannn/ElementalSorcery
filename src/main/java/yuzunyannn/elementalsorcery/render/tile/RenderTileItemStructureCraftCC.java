package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelItemStructureCraftCC;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftCC;

@SideOnly(Side.CLIENT)
public class RenderTileItemStructureCraftCC extends TileEntitySpecialRenderer<TileItemStructureCraftCC>
		implements IRenderItem {

	static public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/is_craft_cc.png");
	static public final ModelItemStructureCraftCC MODEL = new ModelItemStructureCraftCC();

	@Override
	public void render(TileItemStructureCraftCC tile, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, EventClient.tickRender + partialTicks, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true);
	}

}
