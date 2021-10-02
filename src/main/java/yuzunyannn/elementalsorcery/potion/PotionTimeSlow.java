package yuzunyannn.elementalsorcery.potion;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class PotionTimeSlow extends Potion {

	public PotionTimeSlow() {
		super(true, 0xe0e0e0);
		setPotionName("es.effect.timeSlow");
		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "fcf0393c-5a20-4431-8120-df4293a22a58",
				-0.2, 2);
		registerPotionAttributeModifier(SharedMonsterAttributes.FLYING_SPEED, "a4ba9a0b-465b-40c1-8507-2bfd14a2ff1a",
				-0.2, 2);
		registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "e6a8dcf4-87d2-4fe7-9637-d171bbefed58",
				-0.2, 2);
		registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "89e04726-08f3-449c-a341-0c41bd9058b4",
				-0.1, 2);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 5 <= amplifier;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
		RenderObjects.EFFECT_BUFF.bind();
		RenderHelper.drawTexturedModalRect(x + 3, y + 3, 0, 0, 18, 18, 128, 128);
	}

}
