package yuzunyannn.elementalsorcery.capability;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class Adventurer implements IAdventurer, INBTSerializable<NBTTagCompound> {

	@CapabilityInject(IAdventurer.class)
	public static Capability<IAdventurer> ADVENTURER_CAPABILITY;

	protected List<Quest> tasks = new LinkedList<>();
	protected float fame;
	protected float debts;

	@Override
	public Quest getQuest(int index) {
		return tasks.get(index);
	}

	@Override
	public int getQuests() {
		return tasks.size();
	}

	@Override
	public void addQuest(Quest task) {
		tasks.add(task);
	}

	@Override
	public void removeQuest(int index) {
		tasks.remove(index);
	}

	@Override
	public void removeAllQuest() {
		tasks.clear();
	}

	@Override
	public Iterator<Quest> iterator() {
		return tasks.iterator();
	}

	@Override
	public void fame(float count) {
		fame = fame + count;
	}

	@Override
	public float getFame() {
		return fame;
	}

	@Override
	public void setFame(float count) {
		fame = count;
	}

	@Override
	public void incurDebts(int count) {
		this.debts = Math.max(debts + count, 0);
	}

	@Override
	public int getDebts() {
		return MathHelper.ceil(debts);
	}

	@Override
	public void setDebts(int count) {
		debts = count;
	}

	@Override
	public void onUpdate(EntityLivingBase entity) {
		if (entity.world.isRemote) return;
		if (debts <= 0) return;
		if (entity.ticksExisted % (20 * 60) == 0) debts = debts + (debts * ElfChamberOfCommerce.DEBT_INTEREST_PER_SEC);
		if (entity.ticksExisted % (20 * 16) == 0) {
			if (EntityHelper.isCreative(entity)) return;
			if (!ElfChamberOfCommerce.isShouldDebtCollection(entity)) return;
			if (ElfChamberOfCommerce.callDebtCollector(entity)) {
				debts = debts - ElfChamberOfCommerce.COLLECT_DEBT_VALUE;
				if (entity instanceof EntityPlayerMP)
					ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) entity, "elf:deepInDebt");
			}
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) CapabilityProvider.AdventurerProvider.storage.writeNBT(ADVENTURER_CAPABILITY, this,
				null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		CapabilityProvider.AdventurerProvider.storage.readNBT(ADVENTURER_CAPABILITY, this, null, nbt);
	}

	// 能力保存
	public static class Storage implements Capability.IStorage<IAdventurer> {

		@Override
		public NBTBase writeNBT(Capability<IAdventurer> capability, IAdventurer instance, EnumFacing side) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < instance.getQuests(); i++) {
				Quest task = instance.getQuest(i);
				list.appendTag(task.serializeNBT());
			}
			nbt.setTag("tasks", list);
			nbt.setFloat("fame", instance.getFame());
			nbt.setInteger("debts", instance.getDebts());
			return nbt;
		}

		@Override
		public void readNBT(Capability<IAdventurer> capability, IAdventurer instance, EnumFacing side, NBTBase tag) {
			if (tag == null) return;
			NBTTagCompound nbt = (NBTTagCompound) tag;
			NBTTagList list = nbt.getTagList("tasks", NBTTag.TAG_COMPOUND);
			instance.removeAllQuest();
			for (NBTBase base : list) {
				NBTTagCompound data = (NBTTagCompound) base;
				instance.addQuest(new Quest(data));
			}
			instance.setFame(nbt.getFloat("fame"));
			instance.setDebts(nbt.getInteger("debts"));
		}

	}

}
