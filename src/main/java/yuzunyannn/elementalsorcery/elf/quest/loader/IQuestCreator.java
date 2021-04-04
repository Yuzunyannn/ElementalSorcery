package yuzunyannn.elementalsorcery.elf.quest.loader;

import java.util.Map;

import com.google.gson.JsonParseException;

import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.elf.quest.QuestType;

public interface IQuestCreator {

	public QuestType createQuest(Map<String, Object> context) throws QuestCreateFailException, JsonParseException;

	/** 获取，可以生成任务层的名称 */
	public boolean canSpawn(ElfEdificeFloor floor);

	public boolean hasTag(String tag);

}
