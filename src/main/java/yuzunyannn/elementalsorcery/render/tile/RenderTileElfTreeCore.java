package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelBlock;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;

@SideOnly(Side.CLIENT)
public class RenderTileElfTreeCore extends TileEntitySpecialRenderer<TileElfTreeCore> implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/elf_tree_core.png");
	public static final ModelBase MODEL = new ModelBlock();

	@Override
	public void render(TileElfTreeCore tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y + 0.5, z + 0.5, 0.03, alpha);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.SRC_ALPHA);
		GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks), 0, 1, 0);
		GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks) + 45, 1, 0, 1);
		MODEL.render(null, EventClient.getGlobalRotateInRender(partialTicks), 0, 0, 0, 0, 1.0f);
		GlStateManager.disableBlend();
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {

	}

}
