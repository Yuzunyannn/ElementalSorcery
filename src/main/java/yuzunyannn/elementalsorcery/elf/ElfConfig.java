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
			if (isVeryDishonest(player)) ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "elf:dishonest");
		}
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

	public static float getPlayerFame(EntityLivingBase player) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return 1;
		return adventurer.getFame();
	}

}
