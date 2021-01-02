package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.quest.QuestCondition;
import yuzunyannn.elementalsorcery.elf.quest.QuestConditionDelegate;
import yuzunyannn.elementalsorcery.elf.quest.QuestConditionNeedExplore;
import yuzunyannn.elementalsorcery.elf.quest.QuestConditionNeedItem;
import yuzunyannn.elementalsorcery.elf.quest.QuestConditionPlayerLevel;
import yuzunyannn.elementalsorcery.elf.quest.QuestConditionSendAnyParcel;
import yuzunyannn.elementalsorcery.elf.quest.QuestReward;
import yuzunyannn.elementalsorcery.elf.quest.QuestRewardCoin;
import yuzunyannn.elementalsorcery.elf.quest.QuestRewardElfTreeInvest;
import yuzunyannn.elementalsorcery.elf.quest.QuestRewardExp;
import yuzunyannn.elementalsorcery.elf.quest.QuestRewardItem;
import yuzunyannn.elementalsorcery.elf.quest.QuestRewardItemParcel;
import yuzunyannn.elementalsorcery.elf.quest.QuestRewardNextQuest;
import yuzunyannn.elementalsorcery.elf.quest.QuestRewardNextQuestId;
import yuzunyannn.elementalsorcery.elf.quest.QuestRewardTopic;

public class QuestRegister {

	private static void register(String id, Class<? extends QuestCondition> cls) {
		QuestCondition.REGISTRY.register(new ResourceLocation(ElementalSorcery.MODID, id), cls);
	}

	private static void registerReward(String id, Class<? extends QuestReward> cls) {
		QuestReward.REGISTRY.register(new ResourceLocation(ElementalSorcery.MODID, id), cls);
	}

	static public void registerAll() {
		register("PlayerLevel", QuestConditionPlayerLevel.class);
		register("NeedItem", QuestConditionNeedItem.class);
		register("Delegate", QuestConditionDelegate.class);
		register("SendAnyParcel", QuestConditionSendAnyParcel.class);
		register("NeedExplore", QuestConditionNeedExplore.class);

		registerReward("Item", QuestRewardItem.class);
		registerReward("Coin", QuestRewardCoin.class);
		registerReward("Exp", QuestRewardExp.class);
		registerReward("TreeInvest", QuestRewardElfTreeInvest.class);
		registerReward("Topic", QuestRewardTopic.class);
		registerReward("ItemParcel", QuestRewardItemParcel.class);
		registerReward("NextQuest", QuestRewardNextQuest.class);
		registerReward("NextQuestId", QuestRewardNextQuestId.class);

	}
}
