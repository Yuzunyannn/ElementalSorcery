package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GLabel extends GNode {

	protected String text = "";
	protected int wrapWidth = 0;

	public GLabel() {

	}

	public GLabel(String text) {
		this.setString(text);
	}

	public void setString(String text) {
		this.text = text;
		this.resetWidth();
	}

	public void setWrapWidth(int wrapWidth) {
		this.wrapWidth = wrapWidth;
		this.resetWidth();
	}

	protected void resetWidth() {
		this.width = mc.fontRenderer.getStringWidth(text);
		this.height = mc.fontRenderer.FONT_HEIGHT;
		if (this.wrapWidth > 0) {
			if (this.width > this.wrapWidth) {
				this.width = this.wrapWidth;
				this.height = mc.fontRenderer.getWordWrappedHeight(text, wrapWidth);
			}
		}
	}

	@Override
	protected void render(float partialTicks) {
		int a = ((int) Math.max(5, this.rAlpha * 255)) << 24;
		int x = -(int) (this.width * this.anchorX);
		int y = -(int) (this.height * this.anchorY);
		if (wrapWidth > 0) {
			FontRenderer fontRenderer = mc.fontRenderer;
			int color = this.color.toInt() | a;
			for (String s : fontRenderer.listFormattedStringToWidth(text, wrapWidth)) {
				fontRenderer.drawString(s, x, y, color);
				y += fontRenderer.FONT_HEIGHT;
			}
		} else mc.fontRenderer.drawString(text, x, y, this.color.toInt() | a);
	}

}
