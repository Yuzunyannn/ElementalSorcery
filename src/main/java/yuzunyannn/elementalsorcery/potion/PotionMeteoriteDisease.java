package yuzunyannn.elementalsorcery.potion;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class PotionMeteoriteDisease extends PotionCommon {

	public PotionMeteoriteDisease() {
		super(true, 0x2c2828, "meteoriteDisease");
		iconIndex = 27;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration == 1 || (duration % 200 == 0);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		if (entityLivingBaseIn.world.isRemote) return;

		PotionEffect effect = entityLivingBaseIn.getActivePotionEffect(this);
		int duration = effect == null ? 1 : effect.getDuration();

		if (duration == 1) {
			deepen(entityLivingBaseIn, 1);
			return;
		}

		Random rand = entityLivingBaseIn.getRNG();

		int level = 0;
		int time = 20 * 5;

		if (amplifier >= 10) {

			if (rand.nextInt(2) == 0) {
				effect = new PotionEffect(MobEffects.WEAKNESS, 20 * 20, 0);
				entityLivingBaseIn.addPotionEffect(effect);
			}

			if (rand.nextInt(4) == 0) {
				effect = new PotionEffect(MobEffects.HUNGER, 20 * 20, 0);
				entityLivingBaseIn.addPotionEffect(effect);
			}

			level = 1;
			time = 20 * 10;
		}

		if (amplifier >= 20) {

			effect = new PotionEffect(MobEffects.WEAKNESS, 20 * 30, 1);
			entityLivingBaseIn.addPotionEffect(effect);

			effect = new PotionEffect(MobEffects.SLOWNESS, 20 * 30, 1);
			entityLivingBaseIn.addPotionEffect(effect);

			if (rand.nextInt(2) == 0) {
				effect = new PotionEffect(MobEffects.POISON, 20 * 30, 1);
				entityLivingBaseIn.addPotionEffect(effect);
			}

			if (rand.nextInt(4) == 0) {
				effect = new PotionEffect(MobEffects.NAUSEA, 20 * 30, 0);
				entityLivingBaseIn.addPotionEffect(effect);
			}

			if (rand.nextInt(4) == 0) {
				effect = new PotionEffect(MobEffects.WITHER, 20 * 30, 1);
				entityLivingBaseIn.addPotionEffect(effect);
			}

		}

		switch (rand.nextInt(4)) {
		case 0:
			effect = new PotionEffect(MobEffects.SPEED, time, level);
			entityLivingBaseIn.addPotionEffect(effect);
			if (rand.nextBoolean()) break;
		case 1:
			effect = new PotionEffect(MobEffects.JUMP_BOOST, time, level);
			entityLivingBaseIn.addPotionEffect(effect);
			if (rand.nextBoolean()) break;
		case 2:
			effect = new PotionEffect(MobEffects.STRENGTH, time, level);
			entityLivingBaseIn.addPotionEffect(effect);
			if (rand.nextBoolean()) break;
		case 3:
			effect = new PotionEffect(MobEffects.RESISTANCE, time, level);
			entityLivingBaseIn.addPotionEffect(effect);
			if (rand.nextBoolean()) break;
		default:
			break;
		}

	}

	// 0~9 10~19 20~29 30~
	public void deepen(EntityLivingBase entityLivingBaseIn, int add) {

		PotionEffect effect = entityLivingBaseIn.getActivePotionEffect(this);
		int amplifier = effect == null ? -1 : effect.getAmplifier();

		amplifier = amplifier + Math.min(add, 1);

		int sec = 0;
		if (amplifier < 10) sec = 30;
		else if (amplifier < 20) sec = 20;
		else if (amplifier < 30) sec = 10;
		else sec = 4;

		effect = new PotionEffect(this, sec * 60 * 20 / 10, amplifier);
		entityLivingBaseIn.addPotionEffect(effect);

		if (amplifier >= 30) {
			effect = entityLivingBaseIn.getActivePotionEffect(ESObjects.POTIONS.DEATH_WATCH);
			if (effect == null) {
				effect = new PotionEffect(ESObjects.POTIONS.DEATH_WATCH, 60 * 20, 3);
				entityLivingBaseIn.addPotionEffect(effect);
			}
		}
	}

}