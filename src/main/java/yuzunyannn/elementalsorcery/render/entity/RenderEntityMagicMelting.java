package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.entity.EntityMagicMelting;

@SideOnly(Side.CLIENT)
public class RenderEntityMagicMelting extends Render<EntityMagicMelting> {

	public RenderEntityMagicMelting(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityMagicMelting entity, double x, double y, double z, float entityYaw, float partialTicks) {
		ItemStack stack = entity.getItem();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		float progress = RenderFriend.getPartialTicks(entity.getProgress(), entity.getProgress() - 1, partialTicks)
				/ (float) entity.getMaxProgress();
		float drift = RenderFriend.getPartialTicks(entity.drift, entity.prevDrift, partialTicks);
		GlStateManager.translate(x, y + 0.85 - 0.3 * progress + Math.sin(drift) * 0.03, z);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		if (stack.getCount() > 16) {
			GlStateManager.translate(0.05, -0.01, 0.05);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		}
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMagicMelting entity) {
		return null;
	}
}
