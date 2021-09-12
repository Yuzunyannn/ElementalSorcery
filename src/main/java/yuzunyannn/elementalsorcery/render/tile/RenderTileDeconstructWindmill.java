package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.item.IWindmillBlade;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelDeconstructWindmill;
import yuzunyannn.elementalsorcery.render.model.ModelDeconstructWindmillBlade;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructWindmill;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

@SideOnly(Side.CLIENT)
public class RenderTileDeconstructWindmill extends TileEntitySpecialRenderer<TileDeconstructWindmill>
		implements IRenderItem {

	static public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/deconstruct_windmill.png");
	static public final ModelDeconstructWindmill MODEL = new ModelDeconstructWindmill();

	static public final ModelDeconstructWindmillBlade MODEL_BLADE = new ModelDeconstructWindmillBlade();
	static public final ResourceLocation TEXTURE_BLADE_NORMAL = TextHelper
			.toESResourceLocation("textures/blocks/windmill_blade.png");

	public RenderTileDeconstructWindmill() {
	}

	@Override
	public void render(TileDeconstructWindmill tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.0625, alpha);

		float rotate = EventClient.getGlobalRotateInRender(partialTicks) / 180 * 3.14f;
		float high = (MathHelper.sin(rotate) + 1) * 2;

		MODEL.render(null, high, 0, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		ItemStack blade = tile.getStack();
		if (!blade.isEmpty()) {
			GlStateManager.pushMatrix();

			GlStateManager.translate(x + 0.5, y + 0.105 + high / 16, z + 0.5);
			yuzunyannn.elementalsorcery.util.render.RenderHelper.layItemPositionFix(blade);
			Minecraft.getMinecraft().getRenderItem().renderItem(blade, ItemCameraTransforms.TransformType.FIXED);

			GlStateManager.popMatrix();
		}

		// 风车叶片部分
		EnumFacing facing = tile.getOrientation();

		rotate = RenderHelper.getPartialTicks(tile.bladeRotate, tile.prevBladeRotate, partialTicks);
		float bladeShiftRate = RenderHelper.getPartialTicks(tile.bladeShiftRate, tile.prevBladeShiftRate, partialTicks);

		if (bladeShiftRate < 0.001f) return;
		if (bladeShiftRate < 0.999f) {
			bladeShiftRate = MathHelper.sin(bladeShiftRate * 3.1415f / 2);
			rotate = rotate + (1 - bladeShiftRate) * 4;
		} else bladeShiftRate = 1;
		float blend = bladeShiftRate;
		float scale = 0.0625f * bladeShiftRate;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 7.5 + high / 10, z + 0.5);
		if (blend < 0.999f) {
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
		}

		IWindmillBlade windmillBlade = tile.getWindmillBlade();
		ResourceLocation texture = windmillBlade == null ? null : windmillBlade.getWindmillBladeSkin();
		texture = texture == null ? TEXTURE_BLADE_NORMAL : texture;

		GlStateManager.color(1, 1, 1, blend);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(facing.getHorizontalAngle() + 90, 0, 1, 0);

		TextureBinder.mc.getTextureManager().bindTexture(texture);
		MODEL_BLADE.render(null, rotate, 0, 0, 0, 0, 1.0f);

		if (blend < 0.999f) {
			GlStateManager.enableAlpha();
			GlStateManager.disableBlend();
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL, false);
	}

}
