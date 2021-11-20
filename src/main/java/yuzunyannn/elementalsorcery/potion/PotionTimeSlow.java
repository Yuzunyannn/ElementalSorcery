package yuzunyannn.elementalsorcery.potion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;

public class PotionTimeSlow extends PotionCommon {

	public PotionTimeSlow() {
		super(true, 0xe0e0e0, "timeSlow");
		iconIndex = 42;

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
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 5 <= amplifier;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
	}

}
