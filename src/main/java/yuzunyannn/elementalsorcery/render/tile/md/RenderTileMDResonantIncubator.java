package yuzunyannn.elementalsorcery.render.tile.md;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDResonantIncubator;
import yuzunyannn.elementalsorcery.tile.md.TileMDResonantIncubator;

public class RenderTileMDResonantIncubator extends RenderTileMDBase<TileMDResonantIncubator> {
	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_resonant_incubator.png");
	protected static final ModelBase MODEL = new ModelMDResonantIncubator();

	@Override
	public void render(TileMDResonantIncubator tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		// 抖动
		float rate = (100 - tile.getStable()) / 100 * 0.6f;
		float zx = rate == 0 ? 0 : MathHelper.cos(EventClient.globalRotate * 3.14f) * rate;
		float zz = rate == 0 ? 0 : MathHelper.sin(EventClient.globalRotate * 3.14f) * rate;

		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		MODEL.render(null, zx, zz, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		ItemStack stack = tile.getStack();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.3, z + 0.5);
		GlStateManager.scale(0.75, 0.75, 0.75);
		GlStateManager.translate(zx * 0.1f, 0, zz * 0.1f);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, false);
	}
}
