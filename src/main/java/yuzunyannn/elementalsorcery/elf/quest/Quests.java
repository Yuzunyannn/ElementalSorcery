package yuzunyannn.elementalsorcery.elf.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import yuzunyannn.elementalsorcery.elf.edifice.ElfEdificeFloor;
import yuzunyannn.elementalsorcery.explore.ExploreStructureFind;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemKeepsakes;
import yuzunyannn.elementalsorcery.item.ItemNatureDust;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
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

	// =======探索类=========
	public static Quest createExploreQuest(boolean isNext, String value, int coin, Biome biome, ItemStack block,
			String structure) {
		QuestType type = new QuestType();
		type.setName("explore");
		type.addCondition(QuestCondition.REGISTRY.newInstance(QuestConditionNeedExplore.class).needBiome(biome)
				.needBlock(block).needStructure(structure).check());
		QuestDescribe describe = type.getDescribe();
		describe.setTitle("quest.explore");
		if (isNext) describe.addDescribe("quest.next.polite." + (rand.nextInt(2) + 1));
		describe.addDescribe(value);
		describe.addDescribe("quest.end.polite." + (rand.nextInt(3) + 1));
		type.addReward(QuestReward.REGISTRY.newInstance(QuestRewardCoin.class).coin(coin));
		if (rand.nextInt(2) == 0) type.addReward(QuestRewardTopic.create("Natural", rand.nextInt(2) + 1));
		return new Quest(type);
	}

	public static Quest createExploreQuest(int coin, Biome biome, ItemStack block, String structure) {
		return createExploreQuest(false, "quest.explore.research", coin, biome, block, structure);
	}

	public static Quest createNewbieItemQuest(EntityPlayer player, String value, List<ItemRec> need,
			ItemStack... give) {
		QuestType type = new QuestType();
		type.setName("newbie");
		type.addCondition(QuestCondition.REGISTRY.newInstance(QuestConditionNeedItem.class).needItem(need));
		QuestDescribe describe = type.getDescribe();
		type.addPrecondition(QuestCondition.REGISTRY.newInstance(QuestConditionDelegate.class).delegate(player));
		describe.setTitle("quest.newbie.task");
		describe.addDescribe(value);
		type.addReward(QuestReward.REGISTRY.newInstance(QuestRewardItemParcel.class).item(give));
		return new Quest(type);
	}

	public static Quest createExploreStructureQuest(boolean isNext, int coin, String structureName,
			ItemStack needBlock) {
		String value = "quest.explore.research";
		switch (structureName) {
		case ExploreStructureFind.ABANDONED_MINE_SHAFT:
		case ExploreStructureFind.OCEAN_MONUMENTS:
		case ExploreStructureFind.ENDER_STRONGHOLD:
		case ExploreStructureFind.NETHER_FORTRESS:
			if (RandomHelper.rand.nextBoolean()) value = "quest.explore.ruins";
			break;
		case ExploreStructureFind.VILLAGE:
		case ExploreStructureFind.MANSION:
			if (RandomHelper.rand.nextBoolean()) value = "quest.explore.custom";
			break;
		}
		Quest quest = createExploreQuest(isNext, value, coin, null, needBlock, structureName);
		QuestType type = quest.getType();
		switch (structureName) {
		case ExploreStructureFind.ENDER_STRONGHOLD:
			if (RandomHelper.rand.nextBoolean()) type.addReward(QuestRewardTopic.create("Ender", rand.nextInt(2) + 1));
			break;
		case ExploreStructureFind.ABANDONED_MINE_SHAFT:
			if (RandomHelper.rand.nextBoolean()) type.addReward(QuestRewardTopic.create("Engine", rand.nextInt(2) + 1));
		case ExploreStructureFind.TEMPLE:
		case ExploreStructureFind.VILLAGE:
		case ExploreStructureFind.NETHER_FORTRESS:
		case ExploreStructureFind.MANSION:
			if (RandomHelper.rand.nextBoolean()) type.addReward(QuestRewardTopic.create("Struct", rand.nextInt(2) + 1));
			break;
		}
		return quest;
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

	// 其余的一些创建添加
	static public final String GET_ROCK_CAMERA_QUEST = "lab:explore#get";
	static public final String GET_ROCK_CAMERA_QUESTS_1 = "lab:explore#1";
	static public final String GET_ROCK_CAMERA_QUESTS_2 = "lab:explore#2";
	static public final String GET_ROCK_CAMERA_QUESTS_3 = "lab:explore#3";
	static public final String GET_ROCK_CAMERA_QUESTS_4 = "lab:explore#4";
	static public final String GET_ROCK_CAMERA_QUESTS_5 = "lab:explore#5";
	static public final String GET_ROCK_CAMERA_QUESTS_6 = "lab:explore#6";
	static {
		QuestRewardNextQuestId.CREATORS.put(GET_ROCK_CAMERA_QUEST, Quests::createGetRockCameraQuest);
		QuestRewardNextQuestId.CREATORS.put(GET_ROCK_CAMERA_QUESTS_1, Quests::createExploreQuests1);
		QuestRewardNextQuestId.CREATORS.put(GET_ROCK_CAMERA_QUESTS_2, Quests::createExploreQuests2);
		QuestRewardNextQuestId.CREATORS.put(GET_ROCK_CAMERA_QUESTS_3, Quests::createExploreQuests3);
		QuestRewardNextQuestId.CREATORS.put(GET_ROCK_CAMERA_QUESTS_4, Quests::createExploreQuests4);
		QuestRewardNextQuestId.CREATORS.put(GET_ROCK_CAMERA_QUESTS_5, Quests::createExploreQuests5);
		QuestRewardNextQuestId.CREATORS.put(GET_ROCK_CAMERA_QUESTS_6, Quests::createExploreQuests6);

	}

	public static Quest createGetRockCameraQuest(Quest lastQuest, EntityPlayer player) {
		World world = player.world;
		List<ItemRec> need = new ArrayList<ItemRec>();
		need.add(new ItemRec(ESInit.ITEMS.ELF_CRYSTAL, 128));
		need.add(new ItemRec(Items.ENDER_PEARL, 2));
		need.add(new ItemRec(Blocks.GLASS_PANE, 8));
		need.add(new ItemRec(ESInit.ITEMS.NATURE_CRYSTAL, 16));
		need.add(new ItemRec(Items.GLOWSTONE_DUST, 32));
		need.add(new ItemRec(Items.REDSTONE, 32));
		Quest subQuest = Quests.createNewbieItemQuest(player, "quest.explore.join", need,
				new ItemStack(ESInit.ITEMS.ROCK_CAMERA), ItemParchment.getParchment(Pages.ROCK_CAMRERA),
				new ItemStack(ESInit.ITEMS.NATURE_DUST, 3, ItemNatureDust.EnumType.EXPLORE.getMetadata()));
		subQuest.getType().getDescribe().setTitle("quest.explore");
		subQuest.setEndTime(world.getWorldTime() + 24000 + world.rand.nextInt(24000 * 3));
		return subQuest;
	}

	private static Quest createExploreQuestsCommont(EntityPlayer player, int coin, int exp, String structure,
			float fail, String onFail, String onSuccess, List<ItemStack> stacks) {
		World world = player.world;
		ItemStack randBlock = stacks.get(world.rand.nextInt(stacks.size()));
		Quest quest = Quests.createExploreStructureQuest(true, coin, structure, randBlock);
		QuestType type = quest.getType();
		type.addPrecondition(QuestCondition.REGISTRY.newInstance(QuestConditionDelegate.class).delegate(player));
		type.addReward(QuestRewardExp.create(exp));
		if (fail > world.rand.nextFloat()) {
			if (onFail != null) type.addReward(QuestRewardNextQuestId.create(onFail));
		} else {
			if (onSuccess != null) type.addReward(QuestRewardNextQuestId.create(onSuccess));
		}
		quest.setEndTime(world.getWorldTime() + 24000 * 4 + world.rand.nextInt(24000 * 10));
		return quest;
	}

	public static Quest createExploreQuests1(Quest lastQuest, EntityPlayer player) {
		Random rand = player.world.rand;
		Quest quest = createExploreQuestsCommont(player, 50 + rand.nextInt(100), 30 + rand.nextInt(50),
				ExploreStructureFind.VILLAGE, 0.5f, GET_ROCK_CAMERA_QUESTS_1, GET_ROCK_CAMERA_QUESTS_2,
				ItemHelper.toList(Blocks.LOG, Blocks.SANDSTONE, Blocks.BOOKSHELF, Blocks.COBBLESTONE, Blocks.CHEST));
		quest.getType().addReward(QuestRewardItemParcel
				.create(ItemHelper.toArray(Items.WHEAT, rand.nextInt(16) + 4, Items.IRON_INGOT, 4 + rand.nextInt(4))));
		return quest;
	}

	public static Quest createExploreQuests2(Quest lastQuest, EntityPlayer player) {
		Random rand = player.world.rand;
		Quest quest = createExploreQuestsCommont(player, 100 + rand.nextInt(150), 60 + rand.nextInt(100),
				ExploreStructureFind.NETHER_FORTRESS, 0.25f, GET_ROCK_CAMERA_QUESTS_2, GET_ROCK_CAMERA_QUESTS_3,
				ItemHelper.toList(Blocks.NETHER_BRICK, Blocks.NETHER_BRICK_FENCE, Blocks.MOB_SPAWNER,
						Blocks.MOB_SPAWNER, Blocks.MOB_SPAWNER));
		quest.getType()
				.addReward(QuestRewardItemParcel.create(ItemHelper.toArray(Items.BLAZE_POWDER, rand.nextInt(4) + 2)));
		return quest;
	}

	public static Quest createExploreQuests3(Quest lastQuest, EntityPlayer player) {
		Random rand = player.world.rand;
		Quest quest = createExploreQuestsCommont(player, 150 + rand.nextInt(300), 120 + rand.nextInt(150),
				ExploreStructureFind.OCEAN_MONUMENTS, 0.3f, GET_ROCK_CAMERA_QUESTS_2, GET_ROCK_CAMERA_QUESTS_5,
				ItemHelper.toList(Blocks.PRISMARINE, Blocks.SEA_LANTERN));
		quest.getType()
				.addReward(QuestRewardItemParcel.create(ItemHelper.toArray(Blocks.SEA_LANTERN, rand.nextInt(2) + 1)));
		return quest;
	}

	public static Quest createExploreQuests4(Quest lastQuest, EntityPlayer player) {
		Random rand = player.world.rand;
		Quest quest = createExploreQuestsCommont(player, 150 + rand.nextInt(300), 120 + rand.nextInt(150),
				ExploreStructureFind.ENDER_STRONGHOLD, 0.3f, GET_ROCK_CAMERA_QUESTS_2, GET_ROCK_CAMERA_QUESTS_5,
				ItemHelper.toList(Blocks.END_PORTAL_FRAME, Blocks.MOB_SPAWNER, Blocks.IRON_BARS));
		quest.getType()
				.addReward(QuestRewardItemParcel.create(ItemHelper.toArray(Items.ENDER_PEARL, rand.nextInt(6) + 3)));
		return quest;
	}

	public static Quest createExploreQuests5(Quest lastQuest, EntityPlayer player) {
		Random rand = player.world.rand;
		Quest quest = createExploreQuestsCommont(player, 150 + rand.nextInt(300), 120 + rand.nextInt(150),
				ExploreStructureFind.ABANDONED_MINE_SHAFT, 0.4f, GET_ROCK_CAMERA_QUESTS_5, GET_ROCK_CAMERA_QUESTS_6,
				ItemHelper.toList(Blocks.RAIL, Blocks.PLANKS, Blocks.CHEST));
		quest.getType().addReward(QuestRewardItemParcel.create(ItemHelper.toArray(Items.REDSTONE, rand.nextInt(6) + 6,
				ItemKeepsakes.create(ItemKeepsakes.EnumType.RELIC_FRAGMENT, rand.nextInt(3) + 1))));
		return quest;
	}

	public static Quest createExploreQuests6(Quest lastQuest, EntityPlayer player) {
//		NBTTagCompound playerData = EventServer.getPlayerNBT(player);
//		if (playerData.getBoolean("GWTX")) return null;
//		playerData.setBoolean("GWTX", true);

		Random rand = player.world.rand;
		Quest quest = createExploreQuestsCommont(player, 200 + rand.nextInt(600), 200 + rand.nextInt(300),
				ExploreStructureFind.MANSION, 0, null, null,
				ItemHelper.toList(Blocks.GLASS_PANE, Blocks.TORCH, Blocks.BOOKSHELF, Blocks.PLANKS, 1, 5));
		QuestType type = quest.getType();
		type.addReward(QuestRewardItemParcel.create(ItemHelper.toArray(Items.DIAMOND, 3 + rand.nextInt(9),
				ESInit.ITEMS.MAGIC_STONE, 6 + rand.nextInt(32), ESInit.ITEMS.NATURE_DUST, 3 + rand.nextInt(6), 2)));
		return quest;
	}

}
