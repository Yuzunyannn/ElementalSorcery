package yuzunyannn.elementalsorcery.potion;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

public class PotionFrozen extends PotionCommon {

	public static final float FACTOR = -0.125f;

	public PotionFrozen() {
		super(true, 0x0c43ba, "frozen");
		iconIndex = 23;
		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "396D7260-9D05-F3D5-D122-7BE9968D0542",
				-0.125, Constants.AttributeModifierOperation.MULTIPLY);
		registerPotionAttributeModifier(SharedMonsterAttributes.FLYING_SPEED, "4E87BFC4-FDF9-01C8-A498-B9D5541C0168",
				-0.125, Constants.AttributeModifierOperation.MULTIPLY);
		registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "5E9A4A85-5C7F-32CA-3471-277FB280EA88",
				-0.125, Constants.AttributeModifierOperation.MULTIPLY);
		registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR_TOUGHNESS, "E70870E5-1114-2A4B-4BEA-9264DAB7C45E",
				-0.125, Constants.AttributeModifierOperation.MULTIPLY);
		registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE,
				"766EA10B-730B-15F2-99D7-EAEBA56DA911", 0.125, Constants.AttributeModifierOperation.MULTIPLY);

	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {

		if (!entityLivingBaseIn.world.isRemote && entityLivingBaseIn.ticksExisted % 20 == 0) {
			if (entityLivingBaseIn.isBurning()) {
				DamageSource ds = entityLivingBaseIn.getLastDamageSource();
				if (ds != null && ds.isFireDamage()) {
					PotionEffect effect = entityLivingBaseIn.getActivePotionEffect(this);
					int duration = effect == null ? 0 : effect.getDuration();
					duration = MathHelper.floor(duration / 1.75f) - 20 * 5;
					entityLivingBaseIn.removePotionEffect(this);
					if (duration > 0) entityLivingBaseIn.addPotionEffect(new PotionEffect(this, duration, amplifier));
					else return;
				}
			} else if (entityLivingBaseIn.isWet()) {
				PotionEffect effect = entityLivingBaseIn.getActivePotionEffect(this);
				int duration = effect == null ? 20 : effect.getDuration();
				duration = (int) Math.min(Short.MAX_VALUE, duration * 1.25f);
				entityLivingBaseIn.addPotionEffect(new PotionEffect(this, duration, amplifier));
			}
		}

		if (entityLivingBaseIn instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLivingBaseIn;
			if (player.capabilities.isFlying) return;
		}
		// 冻结状态更容易被摔死
		if (!entityLivingBaseIn.onGround && !entityLivingBaseIn.hasNoGravity()) {
			amplifier = Math.min(5, amplifier + 1);
			entityLivingBaseIn.motionY -= 0.00125f * amplifier * amplifier;
			if (entityLivingBaseIn.motionY < 0 && entityLivingBaseIn.fallDistance > 0)
				entityLivingBaseIn.fallDistance += 0.05f * amplifier * amplifier;
		}
	}

}