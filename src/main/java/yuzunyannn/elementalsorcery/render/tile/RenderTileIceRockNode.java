package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelTileIceRockNode;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockNode;

@SideOnly(Side.CLIENT)
public class RenderTileIceRockNode extends RenderTileIceRockSendRecv<TileIceRockNode> implements IRenderItem {

	public static final ModelTileIceRockNode MODEL = new ModelTileIceRockNode();
	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/ice_rock/ice_rock_crysta_mask.png");

	public RenderTileIceRockNode() {

	}

	@Override
	public void render(TileIceRockNode tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		tile.isRendered = true;
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);

		float rotation = EventClient.getGlobalRotateInRender(partialTicks);
		float ratio = tile.stockRatio;
		if (tile.spawnRatio < 1) {
			float scale = RenderFriend.getPartialTicks(tile.spawnRatio, tile.prevSpawnRatio, partialTicks);
			GlStateManager.scale(scale, scale, scale);
		}
		GlStateManager.color(0.45f * (1 - ratio) + 0.6f * ratio, 0.7f * (1 - ratio) + 0.43f * ratio,
				0.7f * (1 - ratio) + 0.9f * ratio, alpha);
		MODEL.render(null, rotation, 0, 0, 0, 0, 1);

		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.disableCull();
		GlStateManager.color(0.691f, 0.980f, 0.992f);
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, false, 0.038, 0.0175, 0, 0);
		GlStateManager.enableCull();
	}
}
