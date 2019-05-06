package yuzunyan.elementalsorcery.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

public interface IRenderItem {
	/**
	 * 渲染物品
	 */
	void render(ItemStack stack, float partialTicks);

	static boolean isGUI(ItemStack stack) {
		IBakedModel ibakedmodel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
		return ibakedmodel == null ? false
				: ((ItemRendererModel.ItemRendererBakedModel) ibakedmodel).camera_type == ItemCameraTransforms.TransformType.GUI;
	}
}
