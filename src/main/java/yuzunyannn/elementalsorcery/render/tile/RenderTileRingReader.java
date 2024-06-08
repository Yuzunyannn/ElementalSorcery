package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelRingReader;
import yuzunyannn.elementalsorcery.tile.device.TileRingReader;

@SideOnly(Side.CLIENT)
public class RenderTileRingReader extends TileEntitySpecialRenderer<TileRingReader> implements IRenderItem {

	public static final TextureBinder TEXTURE = RenderTileStoneDecoration.TEXTURE;
	static public final ModelRingReader MODEL = new ModelRingReader();

	@Override
	public void render(TileRingReader tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		if (EventClient.canTickInRender) tile.updateAnimation();
		EnumFacing facing = tile.getFacing();
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRenderForReverseModel(x + 0.5, y, z + 0.5, alpha);
		float openProgress = RenderFriend.getPartialTicks(tile.openProgress, tile.prevOpenProgress, partialTicks);
		GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0);
		MODEL.render(null, EventClient.tickRender + partialTicks, openProgress, 0, 0, 0, 1);
		RenderFriend.endTileEntitySpecialRenderForReverseModel();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
		float itemRotation = RenderFriend.getPartialTicks(tile.readRotation, tile.prevReadRotation, partialTicks);
		float rotation = 180 - facing.getHorizontalAngle();
		float dx = MathHelper.sin(rotation / 180 * 3.1415926f) * 0.02f;
		float dy = MathHelper.cos(rotation / 180 * 3.1415926f) * 0.02f;
		RenderFriend.renderItemLayout(tile.getStack(), x + 0.5 + dx, y - 0.24, z + 0.5 + dy, rotation + itemRotation);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.disableCull();
		RenderFriend.renderSpecialItemForModelForReverseModel(stack, TEXTURE, MODEL, true);
		GlStateManager.enableCull();
	}

}
