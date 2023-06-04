package yuzunyannn.elementalsorcery.potion;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class PotionCommon extends Potion {

	public int iconIndex = 0;

	public PotionCommon(boolean isBadEffectIn, int liquidColorIn, String name) {
		super(isBadEffectIn, liquidColorIn);
		setPotionName("es.effect." + name);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
		RenderObjects.EFFECT_BUFF.bind();
		int c = iconIndex % 7;
		int r = iconIndex / 7;
		RenderFriend.drawTextureModalRect(x + 3, y + 3, 18 * c, 18 * r, 18, 18, 128, 128);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
		RenderObjects.EFFECT_BUFF.bind();
		int c = iconIndex % 7;
		int r = iconIndex / 7;
		RenderFriend.drawTextureModalRect(x + 6, y + 6, 18 * c, 18 * r, 18, 18, 128, 128);

		int amplifier = effect.getAmplifier();
		if (amplifier > 3) {
			String s1 = I18n.format(getName());
			int w = RenderFriend.mc.fontRenderer.getStringWidth(s1);

			String v = " ";
			if (amplifier >= 10) v = v + TextHelper.toRoman(amplifier + 1);
			else v = v + I18n.format("enchantment.level." + (amplifier + 1));
			RenderFriend.mc.fontRenderer.drawStringWithShadow(v, x + 10 + 18 + w, y + 6, 16777215);
		}
	}

}
