package yuzunyannn.elementalsorcery.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityHelper {

	static public boolean isCreative(EntityLivingBase entity) {
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

}
