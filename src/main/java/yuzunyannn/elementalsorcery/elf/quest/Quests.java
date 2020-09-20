package yuzunyannn.elementalsorcery.elf.quest;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.item.ItemQuest;
import yuzunyannn.elementalsorcery.util.item.ItemRec;

public class Quests {

	/** 注销某个生物的全部过期任务 */
	public static void unsignOverdueQuest(EntityLivingBase entity, boolean isReputationDecline) {
		IAdventurer adventurer = entity.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return;
		Iterator<Quest> iter = adventurer.iterator();
		long now = entity.world.getWorldTime();
		while (iter.hasNext()) {
			Quest q = iter.next();
			if (q.isOverdue(now)) {
				if (isReputationDecline) {
					// 这里还需要进行其他数值的操作——+——+——+——+——+——+——
				}
				iter.remove();
			}
		}
	}

	/** 登记一个任务，给某个生物 */
	public static boolean signQuest(EntityLivingBase player, ItemStack questStack) {
		if (!ItemQuest.isQuest(questStack)) return false;
		Quest quest = ItemQuest.getQuest(questStack);
		if (!quest.sign(player)) return false;
		questStack.setTagCompound(quest.serializeNBT());
		return true;
	}

	/*** 完成某个任务 */
	public static boolean finishQuest(EntityLivingBase player, Quest quest, ItemStack questStack) {
		if (quest.getStatus() != QuestStatus.UNDERWAY) return false;
		if (quest.getType().check(quest, player) != null) return false;
		quest.unsign(player);
		quest.getType().reward(quest, player);
		quest.getType().finish(quest, player);
		quest.unsign(player);
		quest.setStatus(QuestStatus.FINISH);
		if (ItemQuest.isQuest(questStack)) questStack.setTagCompound(quest.serializeNBT());
		return true;
	}

	public static final Random rand = new Random();

	public static QuestType createNeedItems(int coin, ItemRec... itemstack) {
		QuestType type = new QuestType();
		type.addCondition(QuestCondition.REGISTRY.newInstance(QuestConditionNeedItem.class).needItem(itemstack));
		QuestDescribe describe = type.getDescribe();
		describe.setTitle("quest.request.collect");
		describe.addDescribe("quest.broken.house");
		describe.addDescribe("quest.need.c1");
		describe.addDescribe("quest.end.polite.1");
		type.addReward(QuestReward.REGISTRY.newInstance(QuestRewardCoin.class).coin(coin));
		return type;
	}

}
