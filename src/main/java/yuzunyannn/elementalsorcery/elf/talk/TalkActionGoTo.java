package yuzunyannn.elementalsorcery.elf.talk;

import java.util.List;

public class TalkActionGoTo implements ITalkAction {

	String label;

	public TalkActionGoTo(String label) {
		this.label = label;
	}

	@Override
	public Object invoke(TalkChapter chapter, TalkChapter.Iter iter, TalkScene originScene, int talkAt) {
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
