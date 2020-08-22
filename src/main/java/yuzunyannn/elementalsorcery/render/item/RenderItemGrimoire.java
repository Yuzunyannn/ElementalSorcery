package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelGrimoire;

@SideOnly(Side.CLIENT)
public class RenderItemGrimoire implements IRenderItem {

	public static final ModelGrimoire MODEL_GRIMOIRE = new ModelGrimoire();

	public RenderItemGrimoire() {

	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderItemGrimoireInfo info = RenderItemGrimoireInfo.getFromStack(stack);
		GlStateManager.pushMatrix();
		GlStateManager.enableCull();
		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate((float) 0.275F, (float) 0.55F, (float) 0.5F);
			GlStateManager.rotate(-55, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-50, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-40, 0.0F, 1.0F, 0.0F);
			info.bind();
			GlStateManager.scale(0.09F, 0.09F, 0.09F);
			MODEL_GRIMOIRE.render(null, 0, 0, 0, 0, 0.0F, 1.0F);
		} else {
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
			flipRight = (flipRight - MathHelper.fastFloor(flipRight)) * 1.6F - 0.3F;
			flipLeft = (flipLeft - MathHelper.fastFloor(flipLeft)) * 1.6F - 0.3F;
			flipRight = MathHelper.clamp(flipRight, 0, 1);
			flipLeft = MathHelper.clamp(flipLeft, 0, 1);
			// 渲染
			GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);
			info.bind();
			MODEL_GRIMOIRE.render(null, dtick, flipRight, flipLeft, spread, 0.0F, 1.0F);
		}
		GlStateManager.popMatrix();
	}
}
