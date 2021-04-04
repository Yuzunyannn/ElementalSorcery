package yuzunyannn.elementalsorcery.elf.quest.loader;

public class QuestCreateFailException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public QuestCreateFailException(String msg) {
		super(msg);
	}

	public QuestCreateFailException(Exception msg) {
		super(msg);
	}

}
