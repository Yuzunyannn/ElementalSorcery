package yuzunyannn.elementalsorcery.elf.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.container.gui.GuiResearch;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;

@SideOnly(Side.CLIENT)
public class TopicDefault extends Topic {

	public float r, g, b;
	public int color;

	public TopicDefault(String type) {
		super(type);
		color = 0xffffff & this.type.hashCode();
		Vec3d c = ColorHelper.color(color);
		r = (float) c.x;
		g = (float) c.y;
		b = (float) c.z;
	}

	public int getColor() {
		return color;
	}

	public int tick;
	public int prevTick;

	public void update(boolean isSelected) {
		prevTick = tick;
		if (isSelected) tick = Math.min(5, tick + 1);
		else tick = Math.max(0, tick - 1);
	}

	public void render(Minecraft mc, float size, float alpha, float partialTicks) {
		mc.getTextureManager().bindTexture(GuiResearch.TEXTURE_01);
		GlStateManager.color(r, g, b, alpha);
		GlStateManager.pushMatrix();
		GlStateManager.scale(size / 64, size / 64, size / 32);
		RenderFriend.drawTextureRectInCenter(0, 0, 64, 64, 0, 0, 32, 32, 128, 128);
		float tick = RenderFriend.getPartialTicks(this.tick, prevTick, partialTicks);
		GlStateManager.color(r, g, b, alpha * tick / 5);
		final int FONT_HEIGHT = mc.fontRenderer.FONT_HEIGHT;
		RenderFriend.drawTextureRectInCenter(tick * 4 - 20, -FONT_HEIGHT + 1, 54, 8, 0, 32, 27, 4, 128, 128);
		RenderFriend.drawTextureRectInCenter(20 - tick * 4, FONT_HEIGHT, 54, 8, 0, 35, 27, 4, 128, 128);
		String str = this.getTranslationKey() + ".name";
		if (I18n.hasKey(str)) str = I18n.format(str);
		else str = this.type;
		mc.fontRenderer.drawString(str, -mc.fontRenderer.getStringWidth(str) / 2, -FONT_HEIGHT / 2, color);
		GlStateManager.popMatrix();
	}

}
