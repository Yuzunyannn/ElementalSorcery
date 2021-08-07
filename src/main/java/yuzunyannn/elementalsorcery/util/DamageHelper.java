package yuzunyannn.elementalsorcery.util;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

public class DamageHelper {

	/** 通用的，魔法伤害 */
	public static DamageSource getMagicDamageSource(@Nullable Entity source, @Nullable Entity directSource) {
		if (directSource == null && source == null) return DamageSource.MAGIC;
		else return DamageSource.causeIndirectMagicDamage(directSource, source);
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

}
