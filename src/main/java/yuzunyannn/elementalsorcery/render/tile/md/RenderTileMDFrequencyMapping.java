package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDFrequencyMapping;
import yuzunyannn.elementalsorcery.tile.md.TileMDFrequencyMapping;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderTileMDFrequencyMapping extends RenderTileMDBase<TileMDFrequencyMapping> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_frequency_mapping.png");
	public static final ModelBase MODEL = new ModelMDFrequencyMapping();

	@Override
	public void render(TileMDFrequencyMapping tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		float high = tile.getHigh(partialTicks);
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, high, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		ItemStack stack = tile.getStack();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.3 + high * 0.0625f * 3, z + 0.5);
		RenderHelper.layItemPositionFix(stack);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		RenderHelper.render(stack, TEXTURE, MODEL, false);
	}
}
