package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestCondition;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionDelegate;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionNeedExplore;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionNeedFame;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionNeedItem;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionPlayerLevel;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionSendAnyParcel;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestReward;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardCoin;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardElfTreeInvest;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardExp;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardFame;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardItem;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardItemParcel;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardNextQuest;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardNextQuestCode;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardTopic;

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
		register("NeedFame", QuestConditionNeedFame.class);

		registerReward("Item", QuestRewardItem.class);
		registerReward("Coin", QuestRewardCoin.class);
		registerReward("Exp", QuestRewardExp.class);
		registerReward("TreeInvest", QuestRewardElfTreeInvest.class);
		registerReward("Topic", QuestRewardTopic.class);
		registerReward("ItemParcel", QuestRewardItemParcel.class);
		registerReward("NextQuest", QuestRewardNextQuest.class);
		registerReward("NextQuestCode", QuestRewardNextQuestCode.class);
		registerReward("Fame", QuestRewardFame.class);
	}
}
