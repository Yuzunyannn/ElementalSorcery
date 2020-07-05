package yuzunyannn.elementalsorcery.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;

/** 成就触发注册 */
public class ESCriteriaTriggers {

	public static final CriterionTriggerAdapter PLAYER_KILLED_ELF = register(new KilledElfTrigger("player_killed_elf"));
	public static final CriterionTriggerAdapter ELF_HURT_PLAYER = register(new KilledElfTrigger("elf_hurt_player"));
	public static final CriterionTriggerAdapter MELT_FIRE = register(new FastTrigger("melt_fire"));
	public static final CriterionTriggerAdapter EAT_ELF_FRUIT = register(new FastTrigger("eat_elf_fruit"));
	public static final CriterionTriggerAdapter NO_CREATIVE = register(new FastTrigger("no_creative"));

	public static void init() {

	}

	public static <T extends ICriterionTrigger> T register(T criterion) {
		return CriteriaTriggers.register(criterion);
	}
}
