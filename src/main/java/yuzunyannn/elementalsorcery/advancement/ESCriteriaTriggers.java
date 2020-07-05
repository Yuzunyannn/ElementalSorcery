package yuzunyannn.elementalsorcery.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;

public class ESCriteriaTriggers {

	public static final CriterionTriggerAdapter PLAYER_KILLED_ELF = register(new KilledElfTrigger("player_killed_elf"));
	public static final CriterionTriggerAdapter MELT_FIRE = register(new CriterionTriggerFast("melt_fire"));
	public static final CriterionTriggerAdapter EAT_ELF_FRUIT = register(new CriterionTriggerFast("eat_elf_fruit"));

	public static void init() {

	}

	public static <T extends ICriterionTrigger> T register(T criterion) {
		return CriteriaTriggers.register(criterion);
	}
}
