package yuzunyannn.elementalsorcery.potion;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

public class PotionElementCrackAttack extends PotionCommon {

	static public final UUID HEALTH_UUID = UUID.fromString("C9BA86CB-F566-CD48-AF52-D0D518E98180");
	static public final UUID MOVE_SPEED_UUID = UUID.fromString("3DC81F1F-82C4-2D7E-7C25-FAC95893C26F");
	static public final UUID FLYING_SPEED_UUID = UUID.fromString("638D8583-467A-BDD1-4C32-4AB66A2F13FB");
	static public final UUID ATTACK_SPEED_UUID = UUID.fromString("1CA4B7FA-D2D2-245E-165B-4CBE24A7AC79");

	public PotionElementCrackAttack() {
		super(true, 0x544f54, "elementCrackAttack");
		iconIndex = 45;

		registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, HEALTH_UUID.toString(), -0.1, 2);
		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, MOVE_SPEED_UUID.toString(), -0.075, 2);
		registerPotionAttributeModifier(SharedMonsterAttributes.FLYING_SPEED, FLYING_SPEED_UUID.toString(), -0.075, 2);
		registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, ATTACK_SPEED_UUID.toString(), -0.05, 2);
	}

	@Override
	public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
		int level = Math.min(amplifier + 1, 9);
		double ratio = (Math.pow(1.04, level * level) - 1) / 1.75;
		return Math.max(modifier.getAmount() * ratio, -0.9);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {

		if (amplifier >= 8) {
			entity.setHealth(0);
			return;
		}

		if (amplifier >= 4) {
			entity.setHealth(entity.getHealth() - entity.getMaxHealth() * 0.01f);
		}

	}

}
