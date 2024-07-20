package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelMantraEmitter;
import yuzunyannn.elementalsorcery.tile.device.TileMantraEmitter;

@SideOnly(Side.CLIENT)
public class RenderTileMantraEmitter extends TileEntitySpecialRenderer<TileMantraEmitter> implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/mantra_emitter.png");
	static public final ModelMantraEmitter MODEL = new ModelMantraEmitter();

	@Override
	public void render(TileMantraEmitter tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRenderForReverseModel(x + 0.5, y, z + 0.5, alpha);
		float yaw = RenderFriend.getPartialTicks(tile.yaw, tile.prevYaw, partialTicks);
		float pitch = RenderFriend.getPartialTicks(tile.pitch, tile.prevPitch, partialTicks);
		float run = RenderFriend.getPartialTicks(tile.runRate, tile.prevRunRate, partialTicks);
		MODEL.render(null, EventClient.tickRender + partialTicks, yaw, pitch, run, 0, 1);
		RenderFriend.endTileEntitySpecialRenderForReverseModel();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.disableCull();
		RenderFriend.renderSpecialItemForModelForReverseModel(stack, TEXTURE, MODEL, true);
		GlStateManager.enableCull();
	}

}
