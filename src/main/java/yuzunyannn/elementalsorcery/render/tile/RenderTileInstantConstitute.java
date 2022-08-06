package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.item.RenderItemGlassCup;
import yuzunyannn.elementalsorcery.render.model.ModelInstantConstituteStela;
import yuzunyannn.elementalsorcery.tile.altar.TileInstantConstitute;
import yuzunyannn.elementalsorcery.util.TextHelper;

@SideOnly(Side.CLIENT)
public class RenderTileInstantConstitute extends TileEntitySpecialRenderer<TileInstantConstitute>
		implements IRenderItem {

	static public final TextureBinder TEXTUREL = new TextureBinder("textures/blocks/instant_constitute.png");
	static public final TextureBinder TEXTUREL_SHINE = new TextureBinder(
			"textures/blocks/instant_constitute_shine.png");
	static public final ModelInstantConstituteStela MODEL = new ModelInstantConstituteStela();
	static final public TextureBinder TEXTURE_FLUID = new TextureBinder("textures/blocks/fluids/order_crystal.png");

	public RenderTileInstantConstitute() {
	}

	@Override
	public void render(TileInstantConstitute tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderFriend.bindDestoryTexture(TEXTUREL, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y + 0.5, z + 0.5, -0.0625, alpha);
		GlStateManager.disableCull();

		MODEL.render(null, 0, 0, 0, 0, 0, 1);

		float ratio = tile.getOderValRationForShow(partialTicks);
		if (ratio > 0 && destroyStage < 0) {
			RenderFriend.disableLightmap(true);

			GlStateManager.translate(0, -0.001, 0);
			TEXTUREL_SHINE.bind();
			MODEL.render(null, 0, 0, 0, 0, 0, 1);
			GlStateManager.translate(0, 0.001, 0);

			GlStateManager.scale(-1.2, -1.2, -1.2);
			GlStateManager.translate(0, -11, 0);
			GlStateManager.color(1, 1, 1, 1);
			TEXTURE_FLUID.bind();
			int frame = (int) ((EventClient.tickRender + partialTicks) / 3);
			RenderItemGlassCup.drawJuiceInCup(0.42f * ratio, -frame);

			RenderFriend.disableLightmap(false);
		}
		GlStateManager.enableCull();
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		if (destroyStage >= 0) return;
		if (this.rendererDispatcher.cameraHitResult != null
				&& tile.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos())) {
			Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
			ItemStack handStack = renderEntity instanceof EntityLivingBase
					? ((EntityLivingBase) renderEntity).getHeldItemMainhand()
					: ItemStack.EMPTY;
			IItemStructure is = ItemStructure.getItemStructure(handStack);
			if (is.isEmpty()) {
				if (ratio > 0) {
					String orderStr = TextHelper.toAbbreviatedNumber(tile.getOrderVal(), 1) + "/"
							+ TextHelper.toAbbreviatedNumber(tile.getMaxOrderVal(), 1);
					this.drawNameplate(tile, orderStr, x, y - 0.35, z, 12);
				}
			} else {
				ItemStack stack = is.getStructureItem(0);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
				GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks), 0, 1, 0);
				GlStateManager.enableColorLogic();
				GlStateManager.colorLogicOp(GlStateManager.LogicOp.AND);
				Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
				GlStateManager.disableColorLogic();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.disableCull();
		RenderFriend.renderSpecialItem(stack, TEXTUREL, MODEL, true, -0.038, -0.0175, 0.35, 0.1);
		GlStateManager.enableCull();
	}

}
