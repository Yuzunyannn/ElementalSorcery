package yuzunyannn.elementalsorcery.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.ItemRendererModel.ItemRendererBakedModel;

@SideOnly(Side.CLIENT)
public interface IRenderItem {
	/**
	 * 渲染物品
	 */
	void render(ItemStack stack, float partialTicks);

	static boolean isGUI(ItemStack stack) {
		return isTransform(stack, ItemCameraTransforms.TransformType.GUI);
	}

	static boolean isTransform(ItemStack stack, ItemCameraTransforms.TransformType type) {
		IBakedModel ibakedmodel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
		return ibakedmodel == null ? false : ((ItemRendererBakedModel) ibakedmodel).cameraType == type;
	}
}
