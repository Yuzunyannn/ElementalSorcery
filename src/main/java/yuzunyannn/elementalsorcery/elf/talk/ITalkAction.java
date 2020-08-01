package yuzunyannn.elementalsorcery.elf.talk;

import net.minecraft.entity.player.EntityPlayer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

/** 描述一种行为 */
public interface ITalkAction {

	/** 执行 */
	Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, TalkChapter.Iter iter, TalkScene scene,
			int talkAt);

	/** 是否为转折点 */
	boolean isPoint();
}
