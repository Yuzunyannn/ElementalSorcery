package yuzunyannn.elementalsorcery.elf.talk;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class TalkActionGoTo implements ITalkAction {

	static public boolean goTo(String label, TalkChapter chapter, TalkScene originScene, TalkChapter.Iter iter) {
		if (label == null) return false;
		List<TalkScene> scenes = chapter.getScenes();
		for (int i = 0; i < scenes.size(); i++) {
			TalkScene scene = scenes.get(i);
			if (label.equals(scene.label) && scene != originScene) {
				iter.setIndex(i);
				return true;
			}
		}
		return false;
	}

	String label;

	public TalkActionGoTo(String label) {
		this.label = label;
	}

	public TalkActionGoTo(JsonObject json) {
		this.label = json.needString("label", "to", "go");
	}

	@Override
	public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, TalkChapter.Iter iter,
			TalkScene originScene, int talkAt) {
		return goTo(label, chapter, originScene, iter);
	}

	@Override
	public boolean isPoint() {
		return true;
	}

}
