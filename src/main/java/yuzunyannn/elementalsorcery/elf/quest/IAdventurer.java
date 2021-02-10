package yuzunyannn.elementalsorcery.elf.quest;

import yuzunyannn.elementalsorcery.config.ESConfig;

public interface IAdventurer extends Iterable<Quest> {

	/** 获取任务数量 */
	public int getQuests();

	/** 获取任务 */
	public Quest getQuest(int index);

	/** 添加任务 */
	public void addQuest(Quest task);

	/** 移除任务 */
	public void removeQuest(int i);

	public void removeAllQuest();

	/** 获取最大任务数量 */
	default public int getMaxQuests() {
		return ESConfig.QUEST_LIMIT;
	}
}
