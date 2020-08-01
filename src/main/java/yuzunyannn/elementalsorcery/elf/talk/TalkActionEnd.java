package yuzunyannn.elementalsorcery.elf.talk;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class TalkActionEnd implements ITalkAction {

	public TalkActionEnd() {
	}

	public TalkActionEnd(JsonObject json) {

	}

	@Override
	public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, TalkChapter.Iter iter,
			TalkScene originScene, int talkAt) {
		List<TalkScene> scenes = chapter.getScenes();
		iter.setIndex(scenes.size());
		return true;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

}
