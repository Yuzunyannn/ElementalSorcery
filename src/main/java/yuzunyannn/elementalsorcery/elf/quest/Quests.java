package yuzunyannn.elementalsorcery.elf.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.item.ItemRec;

public class Quests {
	public static final Random rand = new Random();

	public static Quest createBuildTask(BlockPos corePos, ElfEdificeFloor floorType, int weight,
			List<ItemRec> itemstack) {
		QuestType type = new QuestType();
		type.setName("invest");
		type.addCondition(QuestCondition.REGISTRY.newInstance(QuestConditionNeedItem.class).needItem(itemstack));
		QuestDescribe describe = type.getDescribe();
		describe.setType("elfInvest");
		describe.setTitle("quest.elf.invest");
		describe.addDescribe("quest.elf.invest.want", "floor." + floorType.getUnlocalizedName() + ".name");
		describe.addDescribe("quest.end.polite.2");
		QuestRewardElfTreeInvest reward = QuestReward.REGISTRY.newInstance(QuestRewardElfTreeInvest.class);
		type.addReward(reward.floor(floorType, corePos, weight));
		return new Quest(type);
	}

	public static Quest createPostFirestWelfare(EntityPlayer player) {
		QuestType type = new QuestType();
		type.setName("newbie");
		type.addCondition(QuestCondition.REGISTRY.newInstance(QuestConditionSendAnyParcel.class).needCount(10));
		QuestDescribe describe = type.getDescribe();
		type.addPrecondition(QuestCondition.REGISTRY.newInstance(QuestConditionDelegate.class).delegate(player));
		describe.setTitle("quest.newbie.task");
		describe.addDescribe("quest.post.first.welfare", player.getName());
		ItemStack stack = new ItemStack(ESInit.ITEMS.ELF_STAR);
		type.addReward(QuestReward.REGISTRY.newInstance(QuestRewardItem.class).item(stack));
		return new Quest(type);
	}

	// =======收集物品类=========
	public static Quest createCollectQuest(String value, int politeIndex, int coin, List<ItemRec> itemstack) {
		QuestType type = new QuestType();
		type.setName("collect");
		type.addCondition(QuestCondition.REGISTRY.newInstance(QuestConditionNeedItem.class).needItem(itemstack));
		QuestDescribe describe = type.getDescribe();
		describe.setTitle("quest.request.collect");
		describe.addDescribe(value);
		describe.addDescribe("quest.end.polite." + politeIndex);
		type.addReward(QuestReward.REGISTRY.newInstance(QuestRewardCoin.class).coin(coin));
		return new Quest(type);
	}

	public static Quest createRepair(int coin, List<ItemRec> itemstack) {
		Quest quest = createCollectQuest("quest.broken.house", rand.nextInt(3) + 1, coin, itemstack);
		if (rand.nextInt(3) == 0) quest.getType().addReward(QuestRewardTopic.create("Engine", 1));
		return quest;
	}

	public static Quest createPostOfficeMaterials(int coin, List<ItemRec> itemstack) {
		return createCollectQuest("quest.post.office.want", rand.nextInt(2) + 2, coin, itemstack);
	}

	public static Quest createLibraryMaterials(int coin, List<ItemRec> itemstack) {
		return createCollectQuest("quest.library.want", rand.nextInt(2) + 2, coin, itemstack);
	}

	public static Quest createEnchantedBook(int coin, Enchantment enchantment, int level) {
		List<ItemRec> itemstack = new ArrayList<ItemRec>(1);
		EnchantmentData eData = new EnchantmentData(enchantment, level);
		itemstack.add(new ItemRec(ItemEnchantedBook.getEnchantedItemStack(eData)));
		return createCollectQuest("quest.library.enchant", rand.nextInt(2) + 2, coin, itemstack);
	}

}
