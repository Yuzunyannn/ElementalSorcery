package yuzunyannn.elementalsorcery.elf.quest.reward;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.loader.QuestCreateFailException;
import yuzunyannn.elementalsorcery.init.ESImpClassRegister;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestReward extends ESImpClassRegister.EasyImp<QuestReward> implements INBTSerializable<NBTTagCompound> {

	public static final ESImpClassRegister<QuestReward> REGISTRY = new ESImpClassRegister();

	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		throw new QuestCreateFailException(this.getRegistryName() + " is not finish [initWithConfig] function");
	}

	/** 获得奖励 */
	public void onReward(Quest quest, EntityLivingBase player) {

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
