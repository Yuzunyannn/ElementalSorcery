package yuzunyannn.elementalsorcery.elf.quest;

public enum QuestStatus {
	NONE,
	UNDERWAY,
	FINISH;

	public static QuestStatus get(int ordinal) {
		return QuestStatus.values()[ordinal % QuestStatus.values().length];
	}
}
