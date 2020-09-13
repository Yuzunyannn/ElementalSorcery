package yuzunyannn.elementalsorcery.elf.talk;

import net.minecraft.entity.player.EntityPlayer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

/** 描述一种行为 */
public interface ITalkAction {

	/** 执行 */
	Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, TalkChapter.Iter iter, TalkScene scene,
			int talkAt);

	/** 是否为转折点 */
	default boolean isPoint() {
		return true;
	}

	public static boolean toBoolean(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Boolean) return (boolean) obj;
		return true;
	}
}
