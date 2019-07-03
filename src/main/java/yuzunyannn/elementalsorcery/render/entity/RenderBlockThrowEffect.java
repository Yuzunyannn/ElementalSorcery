package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityBlockThrowEffect;

@SideOnly(Side.CLIENT)
public class RenderBlockThrowEffect extends Render<EntityBlockThrowEffect> {

	public RenderBlockThrowEffect(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBlockThrowEffect entity) {
		return null;
	}

	@Override
	public void doRender(EntityBlockThrowEffect entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.enableRescaleNormal();

		float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
		GlStateManager.rotate(pitch, 1, 0, 1);
		GlStateManager.rotate(entityYaw, 0, 1, 0);

		Minecraft.getMinecraft().getRenderItem().renderItem(entity.stack, ItemCameraTransforms.TransformType.GROUND);

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

}
