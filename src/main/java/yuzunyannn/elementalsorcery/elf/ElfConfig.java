package yuzunyannn.elementalsorcery.elf;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;

public class ElfConfig {
	
	@Config(kind = "global", group = "elf")
	public static boolean PLAY_CAN_ATTACK_ELF = true;

	@Config(kind = "global", group = "elf")
	@Config.NumberRange(min = 0, max = Float.MAX_VALUE)
	public static float FRAME_THRESHOLD_OF_FRIEND = 100;

	@Config(kind = "global", group = "elf")
	public static float FRAME_THRESHOLD_OF_DISHONEST = 0;

	@Config(kind = "global", group = "elf")
	public static float FRAME_THRESHOLD_OF_VERY_DISHONEST = -8;

	@Config(kind = "global", group = "elf")
	public static float FRAME_THRESHOLD_OF_SUPER_DISHONEST = -16;

	@Config(kind = "global", group = "elf")
	public static float FRAME_THRESHOLD_OF_PUBLIC_ENEMY = -100;

	public static void changeFame(EntityLivingBase player, float count) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return;
		adventurer.fame(count);
		if (player instanceof EntityPlayerMP) {
			if (count < 0) {
				if (isPublicEnemy(player))
					ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "elf:publicEnemy");
				else if (isVeryDishonest(player))
					ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "elf:dishonest");
			}
		}
	}

	public static boolean isVeryHonest(EntityLivingBase player) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return false;
		return adventurer.getFame() >= FRAME_THRESHOLD_OF_FRIEND && adventurer.getDebts() == 0;
	}

	public static boolean isDishonest(EntityLivingBase player) {
		return ElfConfig.getPlayerFame(player) < FRAME_THRESHOLD_OF_DISHONEST;
	}

	public static boolean isVeryDishonest(EntityLivingBase player) {
		return ElfConfig.getPlayerFame(player) < FRAME_THRESHOLD_OF_VERY_DISHONEST;
	}

	public static boolean isSuperDishonest(EntityLivingBase player) {
		return ElfConfig.getPlayerFame(player) < FRAME_THRESHOLD_OF_SUPER_DISHONEST;
	}

	public static boolean isPublicEnemy(EntityLivingBase player) {
		return ElfConfig.getPlayerFame(player) <= FRAME_THRESHOLD_OF_PUBLIC_ENEMY;
	}

	public static float getPlayerFame(EntityLivingBase player) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return 1;
		return adventurer.getFame();
	}

}
