package yuzunyannn.elementalsorcery.potion;

import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
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
		RenderHelper.drawTexturedModalRect(x + 3, y + 3, 18 * c, 18 * r, 18, 18, 128, 128);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
		RenderObjects.EFFECT_BUFF.bind();
		int c = iconIndex % 7;
		int r = iconIndex / 7;
		RenderHelper.drawTexturedModalRect(x + 6, y + 6, 18 * c, 18 * r, 18, 18, 128, 128);
//		int amplifier = effect.getAmplifier();
//		if (amplifier > 3) {
//			String s = I18n.format("enchantment.level." + (amplifier + 1));
//			RenderHelper.mc.fontRenderer.drawStringWithShadow(s, x + 4, y + 3, 16777215);
//		}
	}

}