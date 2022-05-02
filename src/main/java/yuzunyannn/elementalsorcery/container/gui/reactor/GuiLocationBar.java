package yuzunyannn.elementalsorcery.container.gui.reactor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.world.WorldLocation;

public class GuiLocationBar {

	private String string = "????";
	private String stringDim = "0";
	private WorldLocation localtion;
	public float x, y;
	public GuiScreen gui;
	public FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	public float alpha, prevAlpha;

	GuiLocationBar(GuiScreen gui) {
		this.gui = gui;
	}

	public void setLoaction(WorldLocation wl) {
		this.localtion = wl;
		BlockPos pos = this.localtion.getPos();
		this.string = pos.getX() + "," + pos.getY() + "," + pos.getZ();
		this.stringDim = String.valueOf(this.localtion.getDimension());
	}

	public WorldLocation getLocaltion() {
		return localtion;
	}

	public void update(int mouseX, int mouseY) {
		prevAlpha = alpha;
		if (isSelect(mouseX, mouseY)) alpha = alpha + (1 - alpha) * 0.5f;
		else alpha = alpha + (0.25f - alpha) * 0.075f;
	}

	public boolean isSelect(int mouseX, int mouseY) {
		mouseX = mouseX - gui.width / 2;
		mouseY = mouseY - gui.height / 2;
		boolean isLeft = x < 0;
		int w = fontRenderer.getStringWidth(string);
		int barWidth = Math.max(46, w + 25);
		float offset = -Math.max(0, barWidth - 46) / 2;
		if (!isLeft) offset = -offset;
		if (mouseX > (x + offset) - barWidth / 2 && mouseX < (x + offset) + barWidth / 2)
			return mouseY > y - 5 && mouseY < y + 5;
		return false;
	}

	public void render(int mouseX, int mouseY, Color color, float alpha, float partialTicks) {
		boolean isLeft = x < 0;
		GlStateManager.translate(x, y, 0);
		GuiElementReactor.COMS.bind();

		float myAlpha = RenderHelper.getPartialTicks(this.alpha, this.prevAlpha, partialTicks);
		alpha = alpha * myAlpha;
		int ialpha = Math.max((int) (alpha * 255), 0x04);
		int iColor = color.toInt() | (ialpha << 24);
		GlStateManager.color(color.r, color.g, color.b, alpha);
		int w = fontRenderer.getStringWidth(string);
		int barWidth = Math.max(46, w + 25);
		float xoff = (barWidth - 46) / 2 + 17.5f;
		float offset = -Math.max(0, barWidth - 46) / 2;
		if (!isLeft) offset = -offset;
		int dw = fontRenderer.getStringWidth(stringDim);
		float dwScale = 1;
		if (dw > 11) dwScale = 11 / (float) dw;
		{
			RenderHelper.drawTexturedRectInCenter(-xoff + offset, 0, 11, 10, 44, 20, 11, 10, 256, 256);
			RenderHelper.drawTexturedRectInCenter(xoff + offset, 0, 11, 10, 79, 20, 11, 10, 256, 256);
			RenderHelper.drawTexturedRectInCenter(offset, 0, barWidth - 22, 10, 55, 20, 24, 10, 256, 256);
		}
		if (isLeft) {
			RenderHelper.drawTexturedRectInCenter(xoff + offset, 0, -11, -10, 44, 30, 11, 10, 256, 256);
			RenderHelper.drawTexturedRectInCenter(-xoff + offset, 0, -11, -10, 79, 30, 11, 10, 256, 256);
			GlStateManager.translate(-w / 2.0f + offset, -4.5, 0);
			fontRenderer.drawString(string, 0, 0, iColor);
			GlStateManager.translate(w / 2.0f - offset, 4.5, 0);

			GlStateManager.translate(offset - barWidth / 2 + (-dw / 2.0f + 5.5f) * dwScale, -4.5 * dwScale, 0);
			if (dwScale != 1) GlStateManager.scale(dwScale, dwScale, dwScale);
			fontRenderer.drawString(stringDim, 0, 0, iColor);
			if (dwScale != 1) {
				float rdwScale = 1 / dwScale;
				GlStateManager.scale(rdwScale, rdwScale, rdwScale);
			}
			GlStateManager.translate(-offset + barWidth / 2 + (dw / 2.0f - 5.5f) * dwScale, 4.5 * dwScale, 0);
		} else {
			RenderHelper.drawTexturedRectInCenter(-xoff + offset, 0, 11, 10, 44, 30, 11, 10, 256, 256);
			RenderHelper.drawTexturedRectInCenter(xoff + offset, 0, 11, 10, 79, 30, 11, 10, 256, 256);
			GlStateManager.translate(-w / 2.0f + offset, -4.5, 0);
			fontRenderer.drawString(string, 0, 0, iColor);
			GlStateManager.translate(w / 2.0f - offset, 4.5, 0);

			GlStateManager.translate(offset + barWidth / 2 + (-dw / 2.0f - 4.5f) * dwScale, -4.5 * dwScale, 0);
			if (dwScale != 1) GlStateManager.scale(dwScale, dwScale, dwScale);
			fontRenderer.drawString(stringDim, 0, 0, iColor);
			if (dwScale != 1) {
				float rdwScale = 1 / dwScale;
				GlStateManager.scale(rdwScale, rdwScale, rdwScale);
			}
			GlStateManager.translate(-offset - barWidth / 2 + (dw / 2.0f + 4.5f) * dwScale, 4.5 * dwScale, 0);
		}

		GlStateManager.translate(-x, -y, 0);
	}

}
