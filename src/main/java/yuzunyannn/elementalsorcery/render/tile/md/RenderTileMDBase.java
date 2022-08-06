package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDBase;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;

public class RenderTileMDBase<T extends TileMDBase> extends TileEntitySpecialRenderer<T> implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_base.png");
	public static final ModelMDBase MODEL_BASE = new ModelMDBase();

	@Override
	public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderFriend.bindDestoryTexture(this.getBaseTexture(), destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL_BASE.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderFriend.renderSpecialItem(stack, this.getBaseTexture(), MODEL_BASE, false);
	}

	protected TextureBinder getBaseTexture() {
		return TEXTURE;
	}
}
