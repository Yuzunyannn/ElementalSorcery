package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.block.altar.BlockElementCube;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelElementTerminal;
import yuzunyannn.elementalsorcery.render.model.ModelElementTerminalCore;
import yuzunyannn.elementalsorcery.tile.device.TileElementTerminal;

@SideOnly(Side.CLIENT)
public class RenderTileElementTerminal extends TileEntitySpecialRenderer<TileElementTerminal> implements IRenderItem {

	public static final TextureBinder TEXTURE_CORE = new TextureBinder("textures/blocks/element_terminal_core.png");
	public static final ModelElementTerminalCore MODEL_CORE = new ModelElementTerminalCore();

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/element_terminal.png");
	public static final ModelElementTerminal MODEL = new ModelElementTerminal();

	@Override
	public void render(TileElementTerminal tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		if (EventClient.canTickInRender) tile.updateAnimation();
		RenderFriend.bindDestoryTexture(RenderTileElementTerminal.TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRenderForReverseModel(x + 0.5, y, z + 0.5, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1);
		RenderFriend.bindDestoryTexture(RenderTileElementTerminal.TEXTURE_CORE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		GlStateManager.translate(0, -2.675, 0);
		final float scale = 0.65f;
		GlStateManager.scale(scale, scale, scale);
		float dtick = EventClient.tickRender + partialTicks;
		float rate = 1;
		MODEL_CORE.render(null, dtick, rate, 0, 0, 0, 1);
		RenderFriend.disableLightmap(true);
		Vec3d color = BlockElementCube.Color.defaultColor.getCoverColor();
		GlStateManager.color((float) color.x, (float) color.y, (float) color.z, 1.0f);
		MODEL_CORE.renderCover(null, dtick, rate, 0, 0, 0, 1);
		RenderFriend.disableLightmap(false);
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
