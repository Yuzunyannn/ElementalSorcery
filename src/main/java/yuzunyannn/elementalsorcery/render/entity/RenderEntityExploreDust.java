package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityExploreDust;
import yuzunyannn.elementalsorcery.logics.EventClient;

@SideOnly(Side.CLIENT)
public class RenderEntityExploreDust extends Render<EntityExploreDust> {

	public RenderEntityExploreDust(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityExploreDust entity) {
		return null;
	}

	@Override
	public void doRender(EntityExploreDust entity, double x, double y, double z, float entityYaw, float partialTicks) {
		ItemStack stack = entity.getStack();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		float r = EventClient.getGlobalRotateInRender(partialTicks);
		GlStateManager.translate(x, y + 0.475 + MathHelper.cos(r / 180 * 3.14f) * 0.25f, z);
		GlStateManager.rotate(r, 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}
}
