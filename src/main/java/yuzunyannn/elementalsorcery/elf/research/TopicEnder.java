package yuzunyannn.elementalsorcery.elf.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.gui.GuiResearch;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

@SideOnly(Side.CLIENT)
public class TopicEnder extends Topic {

	public TopicEnder(String type) {
		super(type);
	}

	public int getColor() {
		return 0x7d1997;
	}

	public int tick;

	public void update(boolean isSelected) {
		if (isSelected || tick != 0) tick = (tick + 1) % 21;
	}

	public void render(Minecraft mc, float size, float alpha, float partialTicks) {
		mc.getTextureManager().bindTexture(GuiResearch.TEXTURE_01);
		GlStateManager.pushMatrix();
		GlStateManager.scale(size / 64, size / 64, size / 32);

		GlStateManager.color(1, 1, 1, alpha);
		RenderHelper.drawTexturedRectInCenter(0, 0, 28, 24, 37, 23, 14, 12, 128, 128);

		if (tick > 0) {
			float at = tick / 5 + (tick % 7 == 0 ? 2 : 0);
			RenderHelper.drawTexturedRectInCenter(at * 25 - 50, 10, 18, 36, 37, 35, 9, 18, 128, 128);
		}

		GlStateManager.popMatrix();
	}

}
