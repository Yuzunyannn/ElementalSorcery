package yuzunyannn.elementalsorcery.elf.quest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestCondition;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestConditionNeedFame;
import yuzunyannn.elementalsorcery.item.ItemQuest;

public class Quest implements INBTSerializable<NBTTagCompound> {

	protected QuestType type = new QuestType();
	protected QuestData data = new QuestData();
	protected QuestStatus status = QuestStatus.NONE;
	protected long endTime = 0;
	/** 任务的标识符 */
	protected short id;
	protected String adventurerName;
	protected UUID adventurer;

	public Quest() {

	}

	public Quest(QuestType type) {
		this.type = type;
	}

	public Quest(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	/** 等级任务给某个玩家 */
	public boolean sign(EntityLivingBase player) {
		if (this.status != QuestStatus.NONE) return false;
		if (adventurer != null) return false;
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return false;
		if (adventurer.getQuests() >= adventurer.getMaxQuests()) return false;
		this.adventurer = player.getUniqueID();
		this.adventurerName = player.getName();
		Set<Short> set = new HashSet<>();
		id = 1;
		for (int i = 0; i < adventurer.getQuests(); i++) {
			Quest q = adventurer.getQuest(i);
			set.add(q.id);
		}
		while (set.contains(id)) id++;
		adventurer.addQuest(this);
		this.setStatus(QuestStatus.UNDERWAY);
		return true;
	}

	public void unsign(EntityLivingBase player) {
		int i = this.findSignQuest(player);
		if (i == -1) return;
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		adventurer.removeQuest(i);
	}

	@Nullable
	public int findSignQuest(EntityLivingBase player) {
		if (!this.isAdventurer(player)) return -1;
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return -1;
		for (int i = 0; i < adventurer.getQuests(); i++) {
			Quest q = adventurer.getQuest(i);
			if (q == this || q.getQusetId() == this.getQusetId()) return i;
		}
		return -1;
	}

	public <T> void trigger(EntityLivingBase player, QuestTrigger<T> type, T data) {
		this.type.trigger(this, player, type, data);
	}

	public QuestData getData() {
		return data;
	}

	public QuestType getType() {
		return type;
	}

	public QuestStatus getStatus() {
		return status;
	}

	public UUID getAdventurerUUID() {
		return adventurer;
	}

	public String getAdventurerName() {
		return adventurerName;
	}

	public boolean isAdventurer(EntityLivingBase player) {
		return player.getUniqueID().equals(this.adventurer);
	}

	public short getQusetId() {
		return id;
	}

	public void setStatus(QuestStatus status) {
		this.status = status;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public boolean isOverdue(long now) {
		if (endTime > 0) return now >= endTime;
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void updateData(NBTTagCompound nbt) {
		data = new QuestData(nbt);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("type", type.serializeNBT());
		if (!data.isEmpty()) nbt.setTag("data", data.serializeNBT());
		nbt.setByte("status", (byte) this.status.ordinal());
		if (endTime > 0) nbt.setLong("end", endTime);
		if (adventurer != null) {
			nbt.setUniqueId("adv", adventurer);
			if (adventurerName != null) nbt.setString("advn", adventurerName);
			nbt.setShort("qid", id);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		type.deserializeNBT(nbt.getCompoundTag("type"));
		data.deserializeNBT(nbt.getCompoundTag("data"));
		status = QuestStatus.get(nbt.getByte("status"));
		endTime = nbt.getLong("end");
		if (nbt.hasKey("qid")) {
			adventurer = nbt.getUniqueId("adv");
			if (nbt.hasKey("advn")) adventurerName = nbt.getString("advn");
			id = nbt.getShort("qid");
		}
	}

	/*** 完成某个任务 */
	public static boolean finishQuest(EntityLivingBase player, Quest quest, ItemStack questStack) {
		if (quest.getStatus() != QuestStatus.UNDERWAY) return false;
		boolean noCheck = false;
		if (player instanceof EntityPlayer) noCheck = ((EntityPlayer) player).isCreative();
		if (!noCheck) {
			int i = quest.findSignQuest(player);
			if (i == -1) return false;
			IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
			if (adventurer == null) return false;
			if (quest.getType().check(adventurer.getQuest(i), player) != null) return false;
		}
		quest.unsign(player);
		quest.getType().reward(quest, player);
		quest.getType().finish(quest, player);
		quest.unsign(player);
		quest.setStatus(QuestStatus.FINISH);
		if (ItemQuest.isQuest(questStack)) {
			questStack.setTagCompound(quest.serializeNBT());
			ItemQuest.setFinish(questStack);
		}
		if (player instanceof EntityPlayerMP)
			ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "quest:" + quest.getType().getName());
		return true;
	}

	/** 登记一个任务，给某个生物 */
	public static boolean signQuest(EntityLivingBase player, ItemStack questStack) {
		if (!ItemQuest.isQuest(questStack)) return false;
		Quest quest = ItemQuest.getQuest(questStack);
		if (!quest.sign(player)) return false;
		questStack.setTagCompound(quest.serializeNBT());
		return true;
	}

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
					float dec = adventurer.getFame() / 100f + 1;
					List<QuestCondition> preconditions = q.getType().getPreconditions();
					for (QuestCondition con : preconditions) {
						if (con instanceof QuestConditionNeedFame) {
							float need = ((QuestConditionNeedFame) con).getFame();
							dec = dec + need / 10;
						}
					}
					ElfConfig.changeFame(entity, -dec);
				}
				iter.remove();
			}
		}
	}

}
