package yuzunyannn.elementalsorcery.elf.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.gui.GuiResearch;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

@SideOnly(Side.CLIENT)
public class TopicStruct extends Topic {

	public TopicStruct(String type) {
		super(type);
	}

	public int getColor() {
		return 0x1992d7;
	}

	public int tick;

	public void update(boolean isSelected) {
		if (isSelected || tick != 0) tick = (tick + 1) % 20;
	}

	public void render(Minecraft mc, float size, float alpha, float partialTicks) {
		mc.getTextureManager().bindTexture(GuiResearch.TEXTURE_01);
		GlStateManager.pushMatrix();
		GlStateManager.scale(size / 64, size / 64, size / 32);

		GlStateManager.color(1, 1, 1, alpha);
		float d = 0;
		if (tick <= 10) {
			if (tick == 0 || tick == 10) d = tick;
			else d = tick + partialTicks;
			GlStateManager.rotate(18f * d, 0, 0, 1);
		} else d = 20 - tick;

		RenderHelper.drawTexturedRectInCenter(7, -7 - d, 3, 25, 55, 0, 3, 25, 128, 128);
		RenderHelper.drawTexturedRectInCenter(-7, 7 + d, 3, 25, 55, 0, 3, 25, 128, 128);
		RenderHelper.drawTexturedRectInCenter(7 + d, 7, 25, 3, 55, 0, 25, 3, 128, 128);
		RenderHelper.drawTexturedRectInCenter(-7 - d, -7, 25, 3, 55, 0, 25, 3, 128, 128);

		GlStateManager.popMatrix();
	}

}
