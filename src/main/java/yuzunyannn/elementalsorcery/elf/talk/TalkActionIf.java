package yuzunyannn.elementalsorcery.elf.talk;

import net.minecraft.entity.player.EntityPlayer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class TalkActionIf implements ITalkAction {

	protected ITalkAction condition;
	protected ITalkAction ifThen, ifElse;

	public TalkActionIf(ITalkAction condition, ITalkAction ifThen, ITalkAction ifElse) {
		this.condition = condition;
		this.ifThen = ifThen;
		this.ifElse = ifElse;
	}

	public TalkActionIf(JsonObject json) {

	}

	@Override
	public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, TalkChapter.Iter iter,
			TalkScene scene, int talkAt) {
		boolean isTrue = false;
		if (condition != null)
			isTrue = ITalkAction.toBoolean(condition.invoke(player, elf, chapter, iter, scene, talkAt));
		if (isTrue) {
			if (ifThen != null) return ifThen.invoke(player, elf, chapter, iter, scene, talkAt);
		} else {
			if (ifElse != null) return ifElse.invoke(player, elf, chapter, iter, scene, talkAt);
		}
		return false;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

}
