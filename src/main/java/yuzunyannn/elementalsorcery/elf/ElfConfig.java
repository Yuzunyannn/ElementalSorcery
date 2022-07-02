package yuzunyannn.elementalsorcery.elf;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;

public class ElfConfig {

	public static void changeFame(EntityLivingBase player, float count) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return;
		adventurer.fame(count);
		if (player instanceof EntityPlayerMP) {
			if (count < 0) {
				if (isPublicEnemy(player)) ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "elf:publicEnemy");
				else if (isVeryDishonest(player))
					ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "elf:dishonest");
			}
		}
	}

	public static boolean isVeryHonest(EntityLivingBase player) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return false;
		return adventurer.getFame() >= 100 && adventurer.getDebts() == 0;
	}

	public static boolean isDishonest(EntityLivingBase player) {
		return ElfConfig.getPlayerFame(player) < 0;
	}

	public static boolean isVeryDishonest(EntityLivingBase player) {
		return ElfConfig.getPlayerFame(player) < -8;
	}

	public static boolean isSuperDishonest(EntityLivingBase player) {
		return ElfConfig.getPlayerFame(player) < -16;
	}

	public static boolean isPublicEnemy(EntityLivingBase player) {
		return ElfConfig.getPlayerFame(player) <= -100;
	}

	public static float getPlayerFame(EntityLivingBase player) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return 1;
		return adventurer.getFame();
	}

}
