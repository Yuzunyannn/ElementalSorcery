package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.client.model.ModelBase;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDMagiclization;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagiclization;

public class RenderTileMDMagiclization extends RenderTileMDBase<TileMDMagiclization> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_magiclization.png");
	protected final ModelBase MODEL = new ModelMDMagiclization();

	@Override
	public void render(TileMDMagiclization tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, EventClient.globalRotate / 10, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, false);
	}

}
