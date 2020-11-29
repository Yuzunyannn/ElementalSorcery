package yuzunyannn.elementalsorcery.elf.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.gui.GuiResearch;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

@SideOnly(Side.CLIENT)
public class TopicEngine extends Topic {

	public TopicEngine(String type) {
		super(type);
	}

	public int getColor() {
		return 0xe9cc07;
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
		float rotate = tick + partialTicks;

		GlStateManager.pushMatrix();
		GlStateManager.rotate(rotate, 0, 0, 1);
		RenderHelper.drawTexturedRectInCenter(0, 0, 32, 32, 32, 0, 23, 23, 128, 128);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(22, 0, 0);
		GlStateManager.rotate(-(rotate + 20), 0, 0, 1);
		RenderHelper.drawTexturedRectInCenter(0, 0, 16, 16, 32, 0, 23, 23, 128, 128);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(-18, 18, 0);
		GlStateManager.rotate(-(rotate + 20), 0, 0, 1);
		RenderHelper.drawTexturedRectInCenter(0, 0, 20, 20, 32, 0, 23, 23, 128, 128);
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
	}

}
