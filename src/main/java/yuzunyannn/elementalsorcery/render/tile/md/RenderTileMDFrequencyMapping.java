package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDFrequencyMapping;
import yuzunyannn.elementalsorcery.tile.md.TileMDFrequencyMapping;

public class RenderTileMDFrequencyMapping extends RenderTileMDBase<TileMDFrequencyMapping> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_frequency_mapping.png");
	public static final ModelBase MODEL = new ModelMDFrequencyMapping();

	@Override
	public void render(TileMDFrequencyMapping tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		float high = tile.getHigh(partialTicks);
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, high, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		ItemStack stack = tile.getStack();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.3 + high * 0.0625f * 3, z + 0.5);
		RenderFriend.layItemPositionFix(stack);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, false);
	}
}
