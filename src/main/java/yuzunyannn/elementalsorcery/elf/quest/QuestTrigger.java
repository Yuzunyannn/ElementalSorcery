package yuzunyannn.elementalsorcery.elf.quest;

import net.minecraft.entity.EntityLivingBase;
import yuzunyannn.elementalsorcery.capability.Adventurer;

public class QuestTrigger<T> {

	/** 进行触发 */
	public void trigger(EntityLivingBase player, T data) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return;
		for (int i = 0; i < adventurer.getQuests(); i++) {
			Quest task = adventurer.getQuest(i);
			task.trigger(player, this, data);
		}
	}
}
