package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelElementPlatform;
import yuzunyannn.elementalsorcery.tile.TileElementPlatform;

@SideOnly(Side.CLIENT)
public class RenderTileElementPlatform extends TileEntitySpecialRenderer<TileElementPlatform> implements IRenderItem {

	static public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/element_platform.png");
	static public final ModelElementPlatform MODEL = new ModelElementPlatform();

	public RenderTileElementPlatform() {

	}

	@Override
	public void render(TileElementPlatform tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);

		float rand = tile.getPos().hashCode() % 360;
		MODEL.render(null, 0, 0, rand + EventClient.getGlobalRotateInRender(partialTicks), 0, 0, 1.0f);

		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		RenderFriend.renderItemLayout(tile.getStack(), x + 0.5, y + 0.55, z + 0.5);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true, 0.038, 0.0175, 0.1, 0);
	}

}
