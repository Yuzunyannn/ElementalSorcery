package yuzunyannn.elementalsorcery.elf.quest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.capability.Adventurer;

public class Quest implements INBTSerializable<NBTTagCompound> {

	protected QuestType type = new QuestType();
	protected QuestData data = new QuestData();
	protected QuestStatus status = QuestStatus.NONE;
	protected long endTime = 0;
	/** 任务的标识符 */
	protected short id;
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
			id = nbt.getShort("qid");
		}
	}

}
