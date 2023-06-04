package yuzunyannn.elementalsorcery.elf.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.container.gui.GuiResearch;

@SideOnly(Side.CLIENT)
public class TopicMantra extends Topic {

	public TopicMantra(String type) {
		super(type);
	}

	public int getColor() {
		return 0xc0c3de;
	}

	public int tick;

	public void update(boolean isSelected) {
		if (isSelected) tick = (tick + 1) % 40;
	}

	public void render(Minecraft mc, float size, float alpha, float partialTicks) {
		mc.getTextureManager().bindTexture(GuiResearch.TEXTURE_01);
		GlStateManager.pushMatrix();
		GlStateManager.scale(size / 64, size / 64, size / 32);
		GlStateManager.color(1, 1, 1, alpha);

		RenderFriend.drawTextureRectInCenter(0, 20, 48, 9, 90, 0, 19, 3, 128, 128);

		float tick = this.tick + partialTicks;
		float xoff = tick / 40.0f * 30;
		float yoff = MathHelper.sin(tick);
		RenderFriend.drawTextureRectInCenter(-5 + xoff, 1 + yoff, 20, 26, 80, 0, 10, 13, 128, 128);

		GlStateManager.popMatrix();
	}

}
