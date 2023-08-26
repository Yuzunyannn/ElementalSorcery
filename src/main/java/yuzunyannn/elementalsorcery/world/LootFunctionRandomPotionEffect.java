package yuzunyannn.elementalsorcery.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class LootFunctionRandomPotionEffect extends LootFunction {

	protected LootFunctionRandomPotionEffect(LootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	public static Potion randomPotionGood(Random rand) {
		switch (rand.nextInt(10)) {
		case 0:
			return MobEffects.REGENERATION;
		case 1:
			return MobEffects.SPEED;
		case 2:
			return MobEffects.LUCK;
		case 3:
			return MobEffects.NIGHT_VISION;
		case 4:
			return ESObjects.POTIONS.POWER_PITCHER;
		case 5:
			return ESObjects.POTIONS.WIND_WALKER;
		case 6:
			return MobEffects.STRENGTH;
		case 7:
			return MobEffects.SATURATION;
		case 8:
			return MobEffects.INVISIBILITY;
		default:
			return MobEffects.INSTANT_HEALTH;
		}
	}

	public static Potion randomPotionBad(Random rand) {
		switch (rand.nextInt(10)) {
		case 0:
			return MobEffects.POISON;
		case 1:
			return MobEffects.SLOWNESS;
		case 2:
			return MobEffects.UNLUCK;
		case 3:
			return MobEffects.BLINDNESS;
		case 4:
			return MobEffects.MINING_FATIGUE;
		case 5:
			return ESObjects.POTIONS.FROZEN;
		case 6:
			return MobEffects.WITHER;
		case 7:
			return MobEffects.WATER_BREATHING;
		case 8:
			return MobEffects.WEAKNESS;
		default:
			return MobEffects.INSTANT_DAMAGE;
		}
	}

	@Override
	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		List<PotionEffect> effects = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Potion potion = rand.nextBoolean() ? randomPotionGood(rand) : randomPotionBad(rand);
			effects.add(new PotionEffect(potion, (rand.nextInt(60) + 10) * 20, rand.nextInt(3)));
			if (rand.nextBoolean()) break;
		}
		PotionUtils.appendEffects(stack, effects);
		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<LootFunctionRandomPotionEffect> {
		public Serializer() {
			super(new ResourceLocation(ESAPI.MODID, "potion_effect_random"), LootFunctionRandomPotionEffect.class);
		}

		public void serialize(JsonObject object, LootFunctionRandomPotionEffect functionClazz,
				JsonSerializationContext serializationContext) {
		}

		public LootFunctionRandomPotionEffect deserialize(JsonObject object,
				JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
			return new LootFunctionRandomPotionEffect(conditionsIn);
		}
	}

}
