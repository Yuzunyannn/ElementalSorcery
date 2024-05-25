package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.IRenderOutline;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.block.device.BlockCloverComputer;
import yuzunyannn.elementalsorcery.render.model.ModelCloverComputer;
import yuzunyannn.elementalsorcery.tile.device.TileCloverComputer;

@SideOnly(Side.CLIENT)
public class RenderTileCloverComputer extends TileEntitySpecialRenderer<TileCloverComputer>
		implements IRenderItem, IRenderOutline<TileCloverComputer> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/a_common_128x128.png");
	static public final ModelCloverComputer MODEL_COMPUTER_A = BlockCloverComputer.MODEL_COMPUTER_A;

	@Override
	public void render(TileCloverComputer tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		EnumFacing facing = tile.getFacing();
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRenderForReverseModel(x + 0.5, y, z + 0.5, alpha);
		GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0);
		MODEL_COMPUTER_A.render(null, 0, 0, 0, 0, 0, 1);
		if (tile.isPowerOn()) {
			RenderFriend.disableLightmap(true);
			MODEL_COMPUTER_A.renderIndicatorLight(1, 1);
			RenderFriend.disableLightmap(false);
		} else MODEL_COMPUTER_A.renderIndicatorLight(0, 1);
		RenderFriend.endTileEntitySpecialRenderForReverseModel();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void renderTileOutline(TileCloverComputer tile, EntityPlayer player, BlockPos pos, float partialTicks) {
		EnumFacing facing = tile.getFacing();
		RenderFriend.RenderCallbacks callback = new RenderFriend.RenderCallbacks() {
			@Override
			public void beforeRender() {
				GlStateManager.scale(-1, -1, 1);
				GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0);
			}

			@Override
			public void endRender() {
				GlStateManager.rotate(-facing.getHorizontalAngle(), 0, 1, 0);
				GlStateManager.scale(-1, -1, 1);
			}
		};

		RenderFriend.renderOutlineByModel(MODEL_COMPUTER_A, player, pos, partialTicks, true, callback);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.disableCull();
		RenderFriend.renderSpecialItemForModelForReverseModel(stack, TEXTURE, MODEL_COMPUTER_A, true);
		GlStateManager.enableCull();
	}

}
