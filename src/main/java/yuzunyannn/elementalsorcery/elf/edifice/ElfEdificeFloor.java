package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.building.BlockItemTypeInfo;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.elf.quest.loader.IQuestCreator;
import yuzunyannn.elementalsorcery.elf.quest.reward.QuestRewardExp;
import yuzunyannn.elementalsorcery.entity.EntityBulletin;
import yuzunyannn.elementalsorcery.init.ESImplRegister;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.item.ItemRec;

public class ElfEdificeFloor extends IForgeRegistryEntry.Impl<ElfEdificeFloor> {

	public static final ESImplRegister<ElfEdificeFloor> REGISTRY = new ESImplRegister(ElfEdificeFloor.class);

	protected String unlocalizedName;

	public ElfEdificeFloor setTranslationKey(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	/** 建筑权重的最低值，超过这个值的建筑才可能被建造 */
	public int getInvestWeight() {
		return 50;
	}

	/** 楼层是否可以被投资 */
	public boolean canInvest(TileElfTreeCore core) {
		int maxCount = this.getMaxCountInTree(core);
		for (FloorInfo info : core.getFloors()) {
			if (info.getType() == this) maxCount--;
			if (maxCount <= 0) return false;
		}
		return true;
	}

	/** 获取本层的投资任务 */
	public Quest getInvestQuest(TileElfTreeCore core, Random rand) {
		// 随机获取一些方块
		Map<ItemRec, Integer> needMap = new HashMap<>();
		FloorInfo info = new FloorInfo(this, BlockPos.ORIGIN);
		IBuilder builder = core.getBuilder(info);
		info.setFloorData(this.createBuildData(builder, rand));
		this.build(builder);
		Map<BlockPos, IBlockState> map = builder.asBlockMap();
		int count = 0;
		int rCount = 0;
		for (Entry<BlockPos, IBlockState> entry : map.entrySet()) {
			count++;
			BlockItemTypeInfo itemInfo = new BlockItemTypeInfo(entry.getValue());
			ItemStack stack = itemInfo.getItemStack();
			if (stack.isEmpty()) continue;
			if (rand.nextInt(5) != 0) continue;
			ItemRec rec = new ItemRec(stack);
			Integer n = needMap.get(rec);
			n = n == null ? 0 : n;
			needMap.put(rec, n + 1);
			rCount++;
		}
		List<ItemRec> needs = new ArrayList<ItemRec>();
		for (Entry<ItemRec, Integer> entry : needMap.entrySet()) {
			ItemRec rec = entry.getKey();
			rec.getItemStack().setCount(entry.getValue());
			needs.add(rec);
		}
		needs.add(new ItemRec(Items.WATER_BUCKET, 1));
		if (rand.nextBoolean()) needs.add(new ItemRec(Items.BEEF, rand.nextInt(5) + 1));
		if (rand.nextBoolean()) needs.add(new ItemRec(Items.CAKE, rand.nextInt(5) + 1));
		int weight = rand.nextInt(16) + (int) (rCount / (float) count * this.getInvestWeight()) + 1;
		// 创建任务
		Quest quest = Quests.createBuildTask(core.getPos(), this, weight, needs);
		quest.getType().addReward(QuestRewardExp.create(100 + rand.nextInt(200)));
		quest.setEndTime(core.getWorld().getWorldTime() + 24000 + rand.nextInt(24000 * 3));
		return quest;
	}

	/**
	 * 获取每一个精灵树里可建造的最多次数<br>
	 * {@link ElfEdificeFloor#canInvest(TileElfTreeCore)}
	 */
	public int getMaxCountInTree(TileElfTreeCore core) {
		return 20;
	}

	/**
	 * 首次生成的时候，可以生成的随机数据，之后的build全部按照该数据进行生成
	 * 
	 * @return 返回通过生成的数据，如果返回null，会自动为该层创建出一个nbt来
	 */
	public NBTTagCompound createBuildData(IBuilder builder, Random rand) {
		return null;
	}

	/** 获取本层的高度，最低为2，至少要可以站下人 */
	public int getFloorHeight(IBuilder builder) {
		return 3;
	}

	/**
	 * 建造一个建筑，可能是翻修的时候也要用到<br/>
	 * 该函数里禁止进行随机，需要随机的内容在{@link ElfEdificeFloor#getBuildData(IBuilder,
	 * Random)中都应该随机完成}
	 * 
	 * @param builder 建造器，可以在这里做到类似于world的操作
	 **/
	public void build(IBuilder builder) {

	}

	/**
	 * 给这个楼层一些玩家可以拿到的东西，会在成功建设之后进行一次调用
	 * 
	 * @param rand 作为奖励等随机的指数
	 * 
	 * 
	 */
	public void surprise(IBuilder builder, Random rand) {

	}

	/** 在本楼层生成实体或一些其他东西 */
	public void spawn(IBuilder builder) {

	}

	public String getTranslationKey() {
		return unlocalizedName;
	}

	public void trySpawnQuest(IBuilder builder, long time) {
		BuilderHelper helper = new BuilderHelper(builder);
		TileElfTreeCore core = helper.treeCore();
		if (core == null) return;

		EntityBulletin bulletin = core.getBulletin();
		if (bulletin == null || bulletin.getQuestCount() > core.getMaxQuestCount()) return;

		World world = core.getWorld();
		Random rand = world.rand;

		List<ResourceLocation> list = new ArrayList<>();
		for (Entry<ResourceLocation, IQuestCreator> entry : Quests.CREATOR.entrySet()) {
			IQuestCreator creator = entry.getValue();
			if (creator.canSpawn(this)) list.add(entry.getKey());
		}

		if (list.isEmpty()) return;

		Quest quest = Quests.createQuest(list.get(rand.nextInt(list.size())), core);
		if (quest == null) return;

		quest.setEndTime(world.getWorldTime() + time);
		bulletin.addQuest(quest);
	}

}
