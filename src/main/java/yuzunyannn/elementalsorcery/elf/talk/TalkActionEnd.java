package yuzunyannn.elementalsorcery.elf.talk;

import java.util.List;

public class TalkActionEnd implements ITalkAction {

	public TalkActionEnd() {
	}


	@Override
	public Object invoke(TalkChapter chapter, TalkChapter.Iter iter, TalkScene originScene, int talkAt) {
		List<TalkScene> scenes = chapter.getScenes();
		iter.setIndex(scenes.size());
		return true;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

}
