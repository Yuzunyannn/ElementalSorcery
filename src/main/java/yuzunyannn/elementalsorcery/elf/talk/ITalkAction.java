package yuzunyannn.elementalsorcery.elf.talk;

/** 描述一种行为 */
public interface ITalkAction {

	/** 执行 */
	Object invoke(TalkChapter chapter, TalkChapter.Iter iter, TalkScene scene, int talkAt);

	/** 是否为转折点 */
	boolean isPoint();
}
