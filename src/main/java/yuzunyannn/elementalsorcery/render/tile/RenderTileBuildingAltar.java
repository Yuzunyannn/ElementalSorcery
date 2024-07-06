package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelBuildingAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileBuildingAltar;

@SideOnly(Side.CLIENT)
public class RenderTileBuildingAltar extends TileEntitySpecialRenderer<TileBuildingAltar> implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/building_altar.png");
	public static final ModelBuildingAltar MODEL = new ModelBuildingAltar();

	@Override
	public void render(TileBuildingAltar tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, EventClient.getGlobalRotateInRender(partialTicks), 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		if (tile.isWorking())
			return;
		ItemStack stack = tile.getStack();
		if (stack.isEmpty())
			return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y - 0.25, z + 0.5);
		yuzunyannn.elementalsorcery.api.util.render.RenderFriend.layItemPositionFix(stack);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true);
	}

}
