package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDHearth;
import yuzunyannn.elementalsorcery.tile.md.TileMDHearth;

public class RenderTileMDHearth extends RenderTileMDBase<TileMDHearth> {
	public static final TextureBinder TEXTURE_OFF = new TextureBinder("textures/blocks/md_hearth_off.png");
	public static final TextureBinder TEXTURE_ON = new TextureBinder("textures/blocks/md_hearth_on.png");
	protected final ModelMDHearth MODEL = new ModelMDHearth();

	@Override
	public void render(TileMDHearth tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		if (tile.isFire())
			RenderFriend.bindDestoryTexture(TEXTURE_ON, destroyStage, rendererDispatcher, DESTROY_STAGES);
		else RenderFriend.bindDestoryTexture(TEXTURE_OFF, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		RenderFriend.renderSpecialItem(stack, TEXTURE_OFF, MODEL, false);
	}
}
