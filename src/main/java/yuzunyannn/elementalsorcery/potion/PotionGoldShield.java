package yuzunyannn.elementalsorcery.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectEntityMapping;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectGoldShield;

public class PotionGoldShield extends PotionCommon {

	public PotionGoldShield() {
		super(false, 0x63b91e, "goldShield");
		this.setBeneficial();
		iconIndex = 25;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isReady(int duration, int amplifier) {
		return duration % 5 == 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void performEffect(EntityLivingBase entity, int amplifier) {
		if (!entity.world.isRemote) return;
		// 偷个鸡，这里client端自动删除
		PotionEffect potion = entity.getActivePotionEffect(this);
		if (potion.getDuration() == 5) entity.removeActivePotionEffect(this);

		EffectGoldShield effect = EffectEntityMapping.getEffect(entity, "goldShield", EffectGoldShield.class);
		if (effect != null) return;
		float height = entity.height;
		effect = new EffectGoldShield(entity.world, new IEffectBinder.EntityBinder(entity, height / 2f));
		effect.defaultScale = height / 1.75f + 0.25f;
		effect.isClientUser = entity == Minecraft.getMinecraft().getRenderViewEntity();
		effect.setCondition(v -> {
			if (entity.isDead) return false;
			return entity.isPotionActive(ESObjects.POTIONS.GOLD_SHIELD);
		});
		Effect.addEffect(effect);
		EffectEntityMapping.setEffect(entity, "goldShield", effect);
	}

}