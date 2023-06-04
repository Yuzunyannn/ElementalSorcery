package yuzunyannn.elementalsorcery.potion;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class PotionSilent extends PotionCommon {

	public PotionSilent() {
		super(true, 0xaaaaaa, "silent");
		iconIndex = 22;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean shouldRenderInvText(PotionEffect effect) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {

		int amplifier = effect.getAmplifier();
		String s1 = I18n.format(getName());
		int w = RenderFriend.mc.fontRenderer.getStringWidth(s1);

		amplifier = Math.min(amplifier, 2);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		{
			float scale = 0.75f;
			RenderObjects.NUMBER_1.bind();
			GlStateManager.color(1, 1, 1, 0.25f);
			RenderFriend.drawTextureModalRect(x + 13, y + 4, (32 * (amplifier % 3)) * scale, 0, 32 * scale, 32 * scale,
					96 * scale, 128 * scale);
		}
		{
			RenderObjects.EFFECT_BUFF.bind();
			int c = iconIndex % 7;
			int r = iconIndex / 7;
			GlStateManager.color(1, 1, 1, 1f);
			RenderFriend.drawTextureModalRect(x + 6, y + 6, 18 * c, 18 * r, 18, 18, 128, 128);
		}
		{
			float scale = 0.25f;
			RenderObjects.NUMBER_1.bind();
			GlStateManager.color(1, 1, 1, 1f);
			RenderFriend.drawTextureModalRect(x + 18, y + 4, (32 * (amplifier % 3)) * scale, 0, 32 * scale, 32 * scale,
					96 * scale, 128 * scale);
		}

		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();

		RenderFriend.mc.fontRenderer.drawStringWithShadow(s1, x + 28, y + 6, 16777215);
		String s = Potion.getPotionDurationString(effect, 1.0F);
		RenderFriend.mc.fontRenderer.drawStringWithShadow(s, x + 28, y + 6 + 10, 8355711);
	}

}