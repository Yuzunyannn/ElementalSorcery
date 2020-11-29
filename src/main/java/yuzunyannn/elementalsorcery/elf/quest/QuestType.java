package yuzunyannn.elementalsorcery.elf.quest;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class QuestType implements INBTSerializable<NBTTagCompound> {

	/** 描述委托人 */
	public class Assignor {

	}

	protected Assignor assignor = new Assignor();
	protected QuestDescribe describe = new QuestDescribe();
	protected ArrayList<QuestCondition> preconditions = new ArrayList<QuestCondition>();
	protected ArrayList<QuestCondition> conditions = new ArrayList<QuestCondition>();
	protected ArrayList<QuestReward> rewards = new ArrayList<QuestReward>();
	protected String name = "";

	public QuestType() {

	}

	public QuestType(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? "" : name;
	}

	public void addPrecondition(QuestCondition condition) {
		condition.asPrecondition(true);
		preconditions.add(condition);
	}

	public void addCondition(QuestCondition condition) {
		conditions.add(condition);
	}

	public void addReward(QuestReward reward) {
		rewards.add(reward);
	}

	public ArrayList<QuestCondition> getPreconditions() {
		return preconditions;
	}

	public ArrayList<QuestCondition> getConditions() {
		return conditions;
	}

	public ArrayList<QuestReward> getRewards() {
		return rewards;
	}

	public QuestDescribe getDescribe() {
		return describe;
	}

	public Assignor getAssignor() {
		return assignor;
	}

	public <T> void trigger(Quest quest, EntityLivingBase player, QuestTrigger<T> type, T data) {
		for (QuestCondition con : this.conditions) con.onTrigger(quest, player, type, data);
	}

	/**
	 * 检查先决条件
	 * 
	 * @return 返回不满足的条件，如果返回null表示全部满足
	 */
	public QuestCondition checkPre(Quest quest, EntityLivingBase player) {
		for (QuestCondition con : this.preconditions) if (!con.onCheck(quest, player)) return con;
		return null;
	}

	public QuestCondition check(Quest quest, EntityLivingBase player) {
		for (QuestCondition con : this.conditions) if (!con.onCheck(quest, player)) return con;
		return null;
	}

	public void finish(Quest quest, EntityLivingBase player) {
		for (QuestCondition con : this.conditions) con.onFinish(quest, player);
	}

	public void reward(Quest quest, EntityLivingBase player) {
		for (QuestReward reward : this.rewards) reward.onReward(quest, player);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		// 类型名
		nbt.setString("name", name);
		// 条件序列化
		NBTTagList preCondition = new NBTTagList();
		nbt.setTag("precon", preCondition);
		for (QuestCondition con : this.preconditions) preCondition.appendTag(con.serializeNBT());
		NBTTagList condition = new NBTTagList();
		nbt.setTag("con", condition);
		for (QuestCondition con : this.conditions) condition.appendTag(con.serializeNBT());
		NBTTagList rewards = new NBTTagList();
		nbt.setTag("rew", rewards);
		for (QuestReward rew : this.rewards) rewards.appendTag(rew.serializeNBT());
		// 其他
		nbt.setTag("describe", describe.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		name = nbt.getString("name");
		// 条件反序列
		NBTTagList preCondition = nbt.getTagList("precon", NBTTag.TAG_COMPOUND);
		this.preconditions.clear();
		for (NBTBase base : preCondition) {
			NBTTagCompound tag = (NBTTagCompound) base;
			ResourceLocation id = new ResourceLocation(tag.getString("id"));
			QuestCondition con = QuestCondition.REGISTRY.newInstance(id);
			if (con == null) continue;
			con.deserializeNBT(tag);
			con.asPrecondition(true);
			this.preconditions.add(con);
		}
		NBTTagList condition = nbt.getTagList("con", NBTTag.TAG_COMPOUND);
		this.conditions.clear();
		for (NBTBase base : condition) {
			NBTTagCompound tag = (NBTTagCompound) base;
			ResourceLocation id = new ResourceLocation(tag.getString("id"));
			QuestCondition con = QuestCondition.REGISTRY.newInstance(id);
			if (con == null) continue;
			con.deserializeNBT(tag);
			this.conditions.add(con);
		}
		// 描述
		this.describe.deserializeNBT(nbt.getCompoundTag("describe"));
		// 奖励
		NBTTagList rewards = nbt.getTagList("rew", NBTTag.TAG_COMPOUND);
		this.rewards.clear();
		for (NBTBase base : rewards) {
			NBTTagCompound tag = (NBTTagCompound) base;
			ResourceLocation id = new ResourceLocation(tag.getString("id"));
			QuestReward rew = QuestReward.REGISTRY.newInstance(id);
			if (rew == null) continue;
			rew.deserializeNBT(tag);
			this.rewards.add(rew);
		}
	}

}
