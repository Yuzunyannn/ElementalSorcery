package yuzunyannn.elementalsorcery.elf.quest.condition;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.elf.quest.Quest;

public class QuestConditionPlayerLevel extends QuestCondition {

	protected int needLevel = 1;

	public QuestConditionPlayerLevel needLevel(int needLevel) {
		this.needLevel = needLevel;
		return this;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("level", needLevel);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		needLevel = Math.max(nbt.getInteger("level"), 1);
	}

	@Override
	public boolean onCheck(Quest task, EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			return player.experienceLevel >= needLevel;
		}
		return false;
	}

}
