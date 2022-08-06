package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelEStoneMatrix;
import yuzunyannn.elementalsorcery.tile.TileEStoneMatrix;

@SideOnly(Side.CLIENT)
public class RenderTileEStoneMatrix extends TileEntitySpecialRenderer<TileEStoneMatrix> implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/estone_matrix.png");
	public static final ModelEStoneMatrix MODEL = new ModelEStoneMatrix();

	@Override
	public void render(TileEStoneMatrix tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y + 0.5, z + 0.5, 0.0625, alpha);
		float ageTick = EventClient.tickRender + partialTicks;
		MODEL.render(null, 0, 0, ageTick, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		ItemStack stack = tile.getStack();
		if (stack.isEmpty()) return;
		RenderFriend.disableLightmap(true);
		float dh = MathHelper.sin(ageTick / 100f) * 0.05f;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5 + dh, z + 0.5);
		GlStateManager.scale(0.3, 0.3, 0.3);
		GlStateManager.rotate(ageTick, 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
		RenderFriend.disableLightmap(false);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true, 0.038, 0.0175, 0.3, 0);
	}
}
