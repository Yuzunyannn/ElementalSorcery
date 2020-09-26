package yuzunyannn.elementalsorcery.elf.quest;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.init.ESImpClassRegister;

public class QuestReward extends ESImpClassRegister.EasyImp<QuestReward> implements INBTSerializable<NBTTagCompound> {

	public static final ESImpClassRegister<QuestReward> REGISTRY = new ESImpClassRegister();

	/** 获得奖励 */
	public void onReward(Quest quest, EntityLivingBase player) {
		if (player instanceof EntityPlayer) this.reward(quest, (EntityPlayer) player);
	}

	public void reward(Quest quest, EntityPlayer player) {

	}

	/** 获取奖励的描述 */
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, @Nullable EntityLivingBase player) {
		return "";
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("id", this.getRegistryName().toString());
		this.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	public void writeToNBT(NBTTagCompound nbt) {

	}

	public void readFromNBT(NBTTagCompound nbt) {

	}
}
