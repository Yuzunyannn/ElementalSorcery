package yuzunyannn.elementalsorcery.elf.talk;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class TalkActionGoTo implements ITalkAction {

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
		if (this.label == null) return false;
		List<TalkScene> scenes = chapter.getScenes();
		for (int i = 0; i < scenes.size(); i++) {
			TalkScene scene = scenes.get(i);
			if (this.label.equals(scene.label) && scene != originScene) {
				iter.setIndex(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

}
