package yuzunyannn.elementalsorcery.potion;

import java.util.Random;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class PotionTideWalker extends Potion {

	public PotionTideWalker() {
		super(true, 0x2f43f4);
		setPotionName("es.effect.tideWalker");
		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "26ee6c1d-e2b9-44ed-ab64-10de3e407e48",
				0.04, 2);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		if (entity.isInWater()) {
			if (entity.motionY < 0) entity.motionY = 0;
			amplifier = Math.min(5, amplifier);
			entity.motionX *= (1.2 + 0.005f * amplifier);
			entity.motionZ *= (1.2 + 0.005f * amplifier);
		}
		if (entity.world.isRemote) {
			Random rand = RandomHelper.rand;
			entity.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, entity.posX + rand.nextGaussian(), entity.posY,
					entity.posZ + rand.nextGaussian(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
		RenderObjects.EFFECT_BUFF.bind();
		RenderHelper.drawTexturedModalRect(x + 2, y + 3, 18 * 2, 0, 18, 18, 128, 128);
	}

}
