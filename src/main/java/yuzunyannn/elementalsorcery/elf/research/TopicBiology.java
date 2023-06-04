package yuzunyannn.elementalsorcery.elf.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.container.gui.GuiResearch;

@SideOnly(Side.CLIENT)
public class TopicBiology extends Topic {

	public TopicBiology(String type) {
		super(type);
	}

	public int getColor() {
		return 0xff7200;
	}

	public int tick;

	public void update(boolean isSelected) {
		if (isSelected) tick++;
	}

	public void render(Minecraft mc, float size, float alpha, float partialTicks) {
		mc.getTextureManager().bindTexture(GuiResearch.TEXTURE_01);
		GlStateManager.pushMatrix();
		GlStateManager.scale(size / 64, size / 64, size / 32);
		GlStateManager.color(1, 1, 1, alpha);

		final int move = 12;
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(-move, -move, 0);
			GlStateManager.rotate(MathHelper.cos(tick * 0.5f + partialTicks) * 20, 0, 0, 1);
			RenderFriend.drawTextureRectInCenter(0, 0, 16, 16, 55 + 0, 25 + 0, 8, 8, 128, 128);
			GlStateManager.popMatrix();
		}
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(move, -move, 0);
			GlStateManager.rotate(MathHelper.sin(tick * 0.5f + partialTicks) * 20, 0, 0, 1);
			RenderFriend.drawTextureRectInCenter(0, 0, 16, 16, 55 + 8, 25 + 8, 8, 8, 128, 128);
			GlStateManager.popMatrix();
		}
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(move, move, 0);
			GlStateManager.rotate(MathHelper.cos(tick * 0.5f + 3.14f / 4 + partialTicks) * 20, 0, 0, 1);
			RenderFriend.drawTextureRectInCenter(0, 0, 16, 16, 55 + 0, 25 + 8, 8, 8, 128, 128);
			GlStateManager.popMatrix();
		}
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(-move, move, 0);
			GlStateManager.rotate(MathHelper.sin(tick * 0.5f + 3.14f / 4 + partialTicks) * 20, 0, 0, 1);
			RenderFriend.drawTextureRectInCenter(0, 0, 16, 16, 55 + 8, 25 + 0, 8, 8, 128, 128);
			GlStateManager.popMatrix();
		}

		GlStateManager.popMatrix();
	}

}
