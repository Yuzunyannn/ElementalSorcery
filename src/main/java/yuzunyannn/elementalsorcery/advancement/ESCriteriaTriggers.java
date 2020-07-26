package yuzunyannn.elementalsorcery.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;

/** 成就触发注册 */
@SuppressWarnings("rawtypes")
public class ESCriteriaTriggers {

	public static final CriterionTriggerAdapter PLAYER_KILLED_ELF = register(new KilledElfTrigger("player_killed_elf"));
	public static final CriterionTriggerAdapter ELF_HURT_PLAYER = register(new KilledElfTrigger("elf_hurt_player"));
	public static final CriterionTriggerAdapter MELT_FIRE = register(new FastTrigger("melt_fire"));
	public static final CriterionTriggerAdapter EAT_ELF_FRUIT = register(new FastTrigger("eat_elf_fruit"));
	public static final CriterionTriggerAdapter NO_CREATIVE = register(new FastTrigger("no_creative"));
	public static final CriterionTriggerAdapter INFUSION_TAKE = register(new InfusionTake("infusion_take"));
	public static final CriterionTriggerAdapter ELEMENT_CRAFT = register(new ElementCraft("element_craft"));
	public static final CriterionTriggerAdapter SPELLBOOK_ENCH = register(new FastTrigger("spellbook_ench"));
	public static final CriterionTriggerAdapter MAGIC_DESK_CRAFT = register(new FastTrigger("magic_desk_craft"));
	public static final CriterionTriggerAdapter GARDEN_BUILD = register(new FastTrigger("garden_build"));

	public static void init() {

	}

	public static <T extends ICriterionTrigger> T register(T criterion) {
		return CriteriaTriggers.register(criterion);
	}
}
