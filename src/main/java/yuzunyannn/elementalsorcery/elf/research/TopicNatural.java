package yuzunyannn.elementalsorcery.elf.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.container.gui.GuiResearch;

@SideOnly(Side.CLIENT)
public class TopicNatural extends Topic {

	public TopicNatural(String type) {
		super(type);
	}

	public int getColor() {
		return 0x008b00;
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
		
		GlStateManager.pushMatrix();
		GlStateManager.rotate(tick + partialTicks, 0, 0, 1);
		RenderFriend.drawTextureRectInCenter(-30, 0, 12, 12, 58, 3, 4, 4, 128, 128);
		RenderFriend.drawTextureRectInCenter(30, 0, 12, 12, 62, 3, 4, 4, 128, 128);
		GlStateManager.popMatrix();

		RenderFriend.drawTextureRectInCenter(0, 0, 60, 39, 60, 9, 20, 13, 128, 128);
		
		GlStateManager.popMatrix();
	}

}
