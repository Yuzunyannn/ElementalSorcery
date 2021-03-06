package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

@SideOnly(Side.CLIENT)
public class EffectFlash extends Effect implements Effect.IGUIEffect {

	public float alpha, prevAlpha;

	public EffectFlash(World world) {
		super(world, 0, 0, 0);
		this.lifeTime = 8;
	}

	@Override
	public void onUpdate() {
		this.lifeTime--;
		prevAlpha = alpha;
		alpha = MathHelper.cos((1 - this.lifeTime / 8f) * 3.14f / 2);
	}

	@Override
	protected void doRender(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		float w = mc.displayWidth;
		float h = mc.displayHeight;
		float a = RenderHelper.getPartialTicks(alpha, prevAlpha, partialTicks);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -100);
		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(0, 0, 0.0D).color(1f, 1f, 1f, a).endVertex();
		bufferbuilder.pos(0, h, 0.0D).color(1f, 1f, 1f, a).endVertex();
		bufferbuilder.pos(w, h, 0.0D).color(1f, 1f, 1f, a).endVertex();
		bufferbuilder.pos(w, 0, 0.0D).color(1f, 1f, 1f, a).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

}
