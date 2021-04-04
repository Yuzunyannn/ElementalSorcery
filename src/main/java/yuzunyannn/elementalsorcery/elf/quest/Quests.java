package yuzunyannn.elementalsorcery.elf.quest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestCondition;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionNeedItem;
import yuzunyannn.elementalsorcery.elf.quest.loader.IQuestCreator;
import yuzunyannn.elementalsorcery.elf.quest.loader.QuestCreateFailException;
import yuzunyannn.elementalsorcery.elf.quest.loader.QuestCreator;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestReward;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardElfTreeInvest;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardFame;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemRec;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.JsonObject;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class Quests {

	static final public Map<ResourceLocation, IQuestCreator> CREATOR = new HashMap<>();

	static public interface IQuestLoader {
		IQuestCreator loadQuest(JsonObject json);
	}

	static public IQuestLoader questLoader = Quests::loadQuest;

	static public void loadAll() throws IOException {
		CREATOR.clear();
		for (ModContainer mod : Loader.instance().getActiveModList()) loadQuest(mod);
	}

	public static void loadQuest(ModContainer mod) {
		Json.ergodicAssets(mod, "/quests", (file, json) -> {
			IQuestCreator creator = questLoader.loadQuest(json);
			CREATOR.put(new ResourceLocation(mod.getModId(), Json.fileToId(file, "/quests")), creator);
			return true;
		});
	}

	public static IQuestCreator loadQuest(JsonObject json) {
		return new QuestCreator(json);
	}

	public static Quest createQuest(String id, EntityLivingBase player) {
		return createQuest(TextHelper.toESResourceLocation(id), player);
	}

	public static Quest createQuest(ResourceLocation id, EntityLivingBase player) {
		return createQuest(id, player, null);
	}

	public static Quest createQuest(ResourceLocation id, EntityLivingBase player, Quest previous) {
		Map<String, Object> context = new HashMap<>();
		context.put("random", player.getRNG());
		context.put("player", player);
		if (previous != null) context.put("previous", previous);
		return createQuest(id, context);
	}

	public static Quest createQuest(ResourceLocation id, TileElfTreeCore core) {
		Map<String, Object> context = new HashMap<>();
		context.put("tree", core);
		context.put("random", core.getWorld().rand);
		return createQuest(id, context);
	}

	@Nullable
	public static Quest createQuest(ResourceLocation id, Map<String, Object> context) {
		try {
			IQuestCreator creator = CREATOR.get(id);
			if (creator == null) throw new QuestCreateFailException("找不到:" + id);
			QuestType questType = creator.createQuest(context);
			return new Quest(questType);
		} catch (QuestCreateFailException e) {
			ElementalSorcery.logger.warn("创建任务失败", e);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("创建任务发生未知异常", e);
		}
		return null;
	}

	public static Quest createBuildTask(BlockPos corePos, ElfEdificeFloor floorType, int weight,
			List<ItemRec> itemstack) {
		QuestType type = new QuestType();
		type.setName("invest");
		type.addCondition(QuestCondition.REGISTRY.newInstance(QuestConditionNeedItem.class).needItem(itemstack));
		QuestDescribe describe = type.getDescribe();
		describe.setTitle("quest.elf.invest");
		describe.addDescribe("quest.elf.invest.want", "floor." + floorType.getUnlocalizedName() + ".name");
		describe.addDescribe("quest.end.polite.2");
		QuestRewardElfTreeInvest reward = QuestReward.REGISTRY.newInstance(QuestRewardElfTreeInvest.class);
		type.addReward(reward.floor(floorType, corePos, weight));
		QuestRewardFame rewardFame = QuestReward.REGISTRY.newInstance(QuestRewardFame.class);
		type.addReward(rewardFame.fame(RandomHelper.rand.nextFloat() * 6 + 2));
		return new Quest(type);
	}

//	public static Quest createExploreStructureQuest(boolean isNext, int coin, String structureName,
//			ItemStack needBlock) {
//		String value = "quest.explore.research";
//		switch (structureName) {
//		case ExploreStructureFind.ABANDONED_MINE_SHAFT:
//		case ExploreStructureFind.OCEAN_MONUMENTS:
//		case ExploreStructureFind.ENDER_STRONGHOLD:
//		case ExploreStructureFind.NETHER_FORTRESS:
//			if (RandomHelper.rand.nextBoolean()) value = "quest.explore.ruins";
//			break;
//		case ExploreStructureFind.VILLAGE:
//		case ExploreStructureFind.MANSION:
//			if (RandomHelper.rand.nextBoolean()) value = "quest.explore.custom";
//			break;
//		}
//		Quest quest = createExploreQuest(isNext, value, coin, null, needBlock, structureName);
//		QuestType type = quest.getType();
//		switch (structureName) {
//		case ExploreStructureFind.ENDER_STRONGHOLD:
//			if (RandomHelper.rand.nextBoolean()) type.addReward(QuestRewardTopic.create("Ender", rand.nextInt(2) + 1));
//			break;
//		case ExploreStructureFind.ABANDONED_MINE_SHAFT:
//			if (RandomHelper.rand.nextBoolean()) type.addReward(QuestRewardTopic.create("Engine", rand.nextInt(2) + 1));
//		case ExploreStructureFind.TEMPLE:
//		case ExploreStructureFind.VILLAGE:
//		case ExploreStructureFind.NETHER_FORTRESS:
//		case ExploreStructureFind.MANSION:
//			if (RandomHelper.rand.nextBoolean()) type.addReward(QuestRewardTopic.create("Struct", rand.nextInt(2) + 1));
//			break;
//		}
//		return quest;

}
