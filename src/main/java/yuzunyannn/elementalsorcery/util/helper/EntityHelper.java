package yuzunyannn.elementalsorcery.util.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EntityHelper {

	static public boolean isCreative(Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			return player.isCreative() || player.isSpectator();
		}
		return false;
	}

	static public boolean isSameTeam(Entity entity1, Entity entity2) {
		if (entity1 == entity2) return true;
		return entity1.isOnSameTeam(entity2);
	}

	static public void setPotionEffectDuration(PotionEffect effect, int duration) {
		try {
			ObfuscationReflectionHelper.setPrivateValue(PotionEffect.class, effect, duration, "field_76460_b");
		} catch (Exception e) {}
	}

}
