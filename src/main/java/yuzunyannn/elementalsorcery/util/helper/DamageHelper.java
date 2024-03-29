package yuzunyannn.elementalsorcery.util.helper;

import javax.annotation.Nullable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.util.world.ElementDamageSource;

public class DamageHelper {

	/** 通用的，魔法伤害 */
	public static DamageSource getMagicDamageSource(@Nullable Entity source, @Nullable Entity directSource) {
		return getDamageSource(ElementStack.magic(1, 1), source, directSource);
	}

	public static DamageSource getVoidDamageSource(@Nullable Entity source, @Nullable Entity directSource) {
		return getDamageSource(ElementStack.EMPTY, source, directSource).setDamageIsAbsolute().setDamageAllowedInCreativeMode();
	}

	/** 元素伤害 */
	public static DamageSource getDamageSource(ElementStack estack, @Nullable Entity source,
			@Nullable Entity directSource) {
		return new ElementDamageSource(estack, source, directSource).setIsThornsDamage().setDamageBypassesArmor()
				.setMagicDamage();
	}

	public static boolean isRuleDamage(DamageSource ds) {
		if (ds.canHarmInCreative()) return ds.getTrueSource() == null;
		return false;
	}

	public static boolean isNormalAttackDamage(DamageSource ds) {
		if (ds.isMagicDamage()) return false;
		String type = ds.getDamageType();
		return "player".equals(type) || "mob".equals(type);
	}

	public static boolean isPhysicalDamage(DamageSource ds) {
		return !isMagicalDamage(ds);
	}

	public static boolean isMagicalDamage(DamageSource ds) {
		return ds.isMagicDamage();
	}

	public static boolean isRangedDamage(DamageSource ds) {
		return ds instanceof EntityDamageSourceIndirect;
	}

	public static float getNormalAttackDamage(EntityLivingBase player, Entity target) {
		if (player == null) return 0;
		// 伤害获取
		float damage = (float) player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		EnumCreatureAttribute spAttribute = EnumCreatureAttribute.UNDEFINED;
		if (target instanceof EntityLivingBase) spAttribute = ((EntityLivingBase) target).getCreatureAttribute();
		float spDamage = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), spAttribute);
		if (damage <= 0 && spDamage <= 0) return 0;
		// 暴击判定
		boolean flag = player.isSprinting();
		boolean vanillaCritical = flag && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder()
				&& !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding()
				&& target instanceof EntityLivingBase;
		vanillaCritical = vanillaCritical && !player.isSprinting();
		if (player instanceof EntityPlayer) {
			CriticalHitEvent hitResult = ForgeHooks.getCriticalHit((EntityPlayer) player, target, vanillaCritical,
					vanillaCritical ? 1.5F : 1.0F);
			vanillaCritical = hitResult != null;
			if (vanillaCritical) damage *= hitResult.getDamageModifier();
		} else damage *= (vanillaCritical ? 1.5F : 1.0F);
		// 最終傷害
		return damage + spDamage;
	}

}
