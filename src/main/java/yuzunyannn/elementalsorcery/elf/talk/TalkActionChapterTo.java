package yuzunyannn.elementalsorcery.elf.talk;

import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

public class TalkActionChapterTo implements ITalkAction {

	Function<Void, TalkChapter> chapterCreator;

	public TalkActionChapterTo(Function<Void, TalkChapter> chapterCreator) {
		this.chapterCreator = chapterCreator;
	}

	@Override
	public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, TalkChapter.Iter iter,
			TalkScene originScene, int talkAt) {
		return this.chapterCreator == null ? null : this.chapterCreator.apply(null);
	}

	@Override
	public boolean isPoint() {
		return true;
	}

}
