package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelElementTranslocator;
import yuzunyannn.elementalsorcery.tile.altar.TileElementTranslocator;

@SideOnly(Side.CLIENT)
public class RenderTileElementTranslocator extends TileEntitySpecialRenderer<TileElementTranslocator>
		implements IRenderItem {

	public final static TextureBinder TEXTURE = new TextureBinder("textures/blocks/element_translocator.png");
	public final static ModelElementTranslocator MODEL = new ModelElementTranslocator();

	@Override
	public void render(TileElementTranslocator tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		float rand = tile.getPos().hashCode();
		MODEL.render(null, 0, 0, rand + EventClient.tickRender + partialTicks, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, true);
	}

}
