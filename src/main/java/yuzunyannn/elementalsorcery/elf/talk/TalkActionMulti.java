package yuzunyannn.elementalsorcery.elf.talk;

import net.minecraft.entity.player.EntityPlayer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class TalkActionMulti implements ITalkAction {

	ITalkAction[] actions;

	public TalkActionMulti(ITalkAction... actions) {
		this.actions = actions;
	}

	public TalkActionMulti(JsonObject json) {

	}

	@Override
	public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, TalkChapter.Iter iter,
			TalkScene scene, int talkAt) {
		boolean ret = false;
		for (ITalkAction action : actions)
			ret &= ITalkAction.toBoolean(action.invoke(player, elf, chapter, iter, scene, talkAt));
		return ret;
	}

	@Override
	public boolean isPoint() {
		for (ITalkAction action : actions) if (action.isPoint()) return true;
		return false;
	}

}
