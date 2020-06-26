package yuzunyannn.elementalsorcery.mods.jei;

import mezz.jei.api.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class DescribeDraw implements IDrawable {

	@Override
	public int getWidth() {
		return 175;
	}

	@Override
	public int getHeight() {
		return 120;
	}

	@Override
	public void draw(Minecraft minecraft, int xOffset, int yOffset) {
		if (drw == null) return;
		xOffset += 5;
		FontRenderer fr = minecraft.fontRenderer;
		DescribeRecipeWrapper.Describe describe = drw.getDescribe();
		String title = I18n.format(describe.title);
		int width = fr.getStringWidth(title);
		fr.drawString(title, xOffset + (this.getWidth() - width) / 2, yOffset, 0);
		String str = I18n.format(describe.value);
		fr.drawSplitString(str, xOffset, yOffset + fr.FONT_HEIGHT + 2, (int) (this.getWidth() * 0.95f), 0);
	}

	public String getTitle() {
		return I18n.format("element.knowledge.name");
	}

	private DescribeRecipeWrapper drw;

	public void setDrw(DescribeRecipeWrapper drw) {
		this.drw = drw;
	}

	class Icon implements IDrawable {

		@Override
		public int getWidth() {
			return 18;
		}

		@Override
		public int getHeight() {
			return 18;
		}

		@Override
		public void draw(Minecraft minecraft, int xOffset, int yOffset) {
			if (drw == null) return;
			net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
			DescribeRecipeWrapper.Describe describe = drw.getDescribe();
			xOffset += 1;
			yOffset += 1;
			minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, describe.machine, xOffset, yOffset);
		}
	}

	public final Icon icon = new Icon();

}
