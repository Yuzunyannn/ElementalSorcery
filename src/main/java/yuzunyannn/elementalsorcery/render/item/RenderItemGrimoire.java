package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.render.model.ModelGrimoire;

@SideOnly(Side.CLIENT)
public class RenderItemGrimoire implements IRenderItem {

	public static final ModelGrimoire MODEL_GRIMOIRE = new ModelGrimoire();

	public RenderItemGrimoire() {

	}

	@Override
	public void render(ItemStack stack, float partialTicks) {

		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (grimoire != null) grimoire.tryLoadState(stack);
		RenderItemGrimoireInfo info = grimoire == null ? RenderItemGrimoireInfo.VEST : grimoire.getRenderInfo();

		GlStateManager.pushMatrix();
		if (IRenderItem.isGUI(stack)) {
			info.bind();
			float spread = info.bookSpreadPrev + (info.bookSpread - info.bookSpreadPrev) * partialTicks;
			float rSpread = 1 - spread;
			GlStateManager.translate(0.325F + spread * 0.15f, 0.55F - spread * 0.1f, 0.5F);
			GlStateManager.rotate(-60 * rSpread, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-50 * rSpread - spread * 60, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-48 * rSpread - spread * 90, 0.0F, 1.0F, 0.0F);
			float flip = info.pageFlipPrev + (info.pageFlip - info.pageFlipPrev) * partialTicks;
			float flipRight = flip + 0.25F;
			float flipLeft = flip + 0.75F;
			flipRight = MathHelper.clamp((flipRight - MathHelper.fastFloor(flipRight)) * 1.6F - 0.3F, 0, 1);
			flipLeft = MathHelper.clamp((flipLeft - MathHelper.fastFloor(flipLeft)) * 1.6F - 0.3F, 0, 1);
			float sacle = 0.04f - spread * 0.007f;
			GlStateManager.scale(sacle, sacle, sacle);
			MODEL_GRIMOIRE.render(null, 0, flipRight, flipLeft, spread, 0.0F, 1.0F);
			this.renderPotent(grimoire, partialTicks, 0, flipRight, flipLeft, spread);
		} else {
			float sacle = 0.0625F / ModelGrimoire.sacle;
			info.bind();
			// 地面上
			if (IRenderItem.isTransform(stack, TransformType.GROUND)) {
				GlStateManager.translate(0.25, 0.5, 0.5);
				GlStateManager.scale(sacle, sacle, sacle);
				MODEL_GRIMOIRE.render(null, 0, 0, 0, 0, 0.0F, 1.0F);
				this.renderPotent(grimoire, partialTicks, 0, 0, 0, 0);
			}
			// 修正，主要是在物品台子上
			else if (IRenderItem.isTransform(stack, TransformType.FIXED)) {
				float dtick = (float) info.tickCount + partialTicks;
				float spread = info.bookSpreadPrev + (info.bookSpread - info.bookSpreadPrev) * partialTicks;
				float flip = info.pageFlipPrev + (info.pageFlip - info.pageFlipPrev) * partialTicks;
				float flipRight = flip + 0.25F;
				float flipLeft = flip + 0.75F;
				flipRight = MathHelper.clamp((flipRight - MathHelper.fastFloor(flipRight)) * 1.6F - 0.3F, 0, 1);
				flipLeft = MathHelper.clamp((flipLeft - MathHelper.fastFloor(flipLeft)) * 1.6F - 0.3F, 0, 1);
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.scale(sacle, sacle, sacle);
				MODEL_GRIMOIRE.render(null, dtick, flipRight, flipLeft, spread, 0.0F, 1.0F);
				this.renderPotent(grimoire, partialTicks, dtick, flipRight, flipLeft, spread);
			}
			// 其他，主要是在手上
			else {
				float dtick = (float) info.tickCount + partialTicks;
				// 计算展开
				float spread = info.bookSpreadPrev + (info.bookSpread - info.bookSpreadPrev) * partialTicks;
				// 设置位移
				GlStateManager.translate((float) 0.3F + spread * 0.2F,
						(float) 0.75F + spread * (MathHelper.sin(dtick * 0.025F) * 0.5F + 0.5F) * 0.15F, (float) 0.6F);
				// 计算旋转
				float rotation = spread * 3.1415926f * 0.5F;
				GlStateManager.rotate(-rotation * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
				// 计算翻页
				float flip = info.pageFlipPrev + (info.pageFlip - info.pageFlipPrev) * partialTicks;
				float flipRight = flip + 0.25F;
				float flipLeft = flip + 0.75F;
				flipRight = MathHelper.clamp((flipRight - MathHelper.fastFloor(flipRight)) * 1.6F - 0.3F, 0, 1);
				flipLeft = MathHelper.clamp((flipLeft - MathHelper.fastFloor(flipLeft)) * 1.6F - 0.3F, 0, 1);
				// 渲染
				GlStateManager.scale(sacle, sacle, sacle);
				MODEL_GRIMOIRE.render(null, dtick, flipRight, flipLeft, spread, 0.0F, 1.0F);
				this.renderPotent(grimoire, partialTicks, dtick, flipRight, flipLeft, spread);
			}
		}
		GlStateManager.popMatrix();
	}

	public void renderPotent(Grimoire grimoire, float partialTicks, float dtick, float flipRight, float flipLeft,
			float spread) {
		if (grimoire == null) return;
		float potent = grimoire.getPotent();
		if (grimoire.potentPoint <= 0.05f || potent <= 0) return;
		GlStateManager.disableLighting();
		GlStateManager.color(1, 1, 1, MathHelper.clamp(potent, 0.1f, 1));
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		float scale = 1.05f + (MathHelper.sin(EventClient.getGlobalRotateInRender(partialTicks) * 0.03f) + 1) * 0.04f;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(-0.001, 0, 0);
		MODEL_GRIMOIRE.render(null, dtick, flipRight, flipLeft, spread, 0.0F, 1.0F);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
	}
}
