package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.container.gui.GuiFairyCube;
import yuzunyannn.elementalsorcery.entity.fcube.IFairyCubeModuleClient;
import yuzunyannn.elementalsorcery.item.ItemFairyCubeModule;
import yuzunyannn.elementalsorcery.render.model.ModelFairyCubeModule;

@SideOnly(Side.CLIENT)
public class RenderItemFairyCubeModule implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/items/fairy_cube_module.png");
	public static final ModelFairyCubeModule MODEL = new ModelFairyCubeModule();

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		TEXTURE.bind();

		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.5, 0.5, 0.5);
			GlStateManager.scale(0.05, 0.05, 0.05);
			GlStateManager.rotate(-90, 1, 0, 0);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		} else {
			if (IRenderItem.isTransform(stack, TransformType.GROUND)) {
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.scale(0.025, 0.025, 0.025);
				MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			} else if (IRenderItem.isTransform(stack, TransformType.FIXED)) {
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.rotate(-90, 1, 0, 0);
				GlStateManager.scale(0.05, 0.05, 0.05);
				MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			} else {
				GlStateManager.translate(0.5, 0.52, 0.5);
				GlStateManager.scale(0.03, 0.03, 0.03);
				MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			}
		}
		renderIcon(stack);
		GlStateManager.popMatrix();
		GlStateManager.enableCull();
	}

	private void renderIcon(ItemStack stack) {
		ResourceLocation id = ItemFairyCubeModule.getModuleId(stack);
		if (id == null) return;
		IFairyCubeModuleClient render = IFairyCubeModuleClient.get(id);
		if (render == null) return;
		TextureBinder.mc.getTextureManager().bindTexture(GuiFairyCube.TEXTURE);
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.scale(0.25, 0.25, 0.25);
		GlStateManager.translate(0, 7, 1);
		render.doRenderIcon();
	}

}
