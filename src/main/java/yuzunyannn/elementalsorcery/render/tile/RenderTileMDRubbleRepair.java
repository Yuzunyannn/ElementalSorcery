package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.render.model.ModelMDRubbleRepair;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderTileMDRubbleRepair extends RenderTileMDBase<TileMDRubbleRepair> {
	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_rubble_repair.png");
	protected final ModelMDRubbleRepair MODEL = new ModelMDRubbleRepair();

	@Override
	public void render(TileMDRubbleRepair tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		ItemStack stack = tile.getStack();
		if (stack.isEmpty())
			return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.475, z + 0.5);
		GlStateManager.scale(0.5, 0.5, 0.5);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		RenderHelper.render(stack, TEXTURE, MODEL, false);
	}
}
