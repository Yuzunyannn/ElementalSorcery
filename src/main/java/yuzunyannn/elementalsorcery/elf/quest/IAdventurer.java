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

	/** 获取信誉 */
	public float getFame();

	public void setFame(float count);

	/** 增加或减少信誉 */
	public void fame(float count);

	/** 增减负债值，正为增 */
	public void incurDebts(int count);

	/** 获取负债值，最小为0 */
	public int getDebts();
}
