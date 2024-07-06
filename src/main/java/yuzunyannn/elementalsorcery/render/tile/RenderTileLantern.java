package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.ModelLantern;
import yuzunyannn.elementalsorcery.tile.TileLantern;

@SideOnly(Side.CLIENT)
public class RenderTileLantern extends TileEntitySpecialRenderer<TileLantern> implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/lantern.png");
	private final ModelLantern MODEL = new ModelLantern();

	@Override
	public void render(TileLantern tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y + 0.0625, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true, 0.0375, 0.0175, 0.05, 0);
	}

}
