package yuzunyannn.elementalsorcery.elf.quest;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.init.ESImpClassRegister;

public class QuestCondition extends ESImpClassRegister.EasyImp<QuestCondition>
		implements INBTSerializable<NBTTagCompound> {

	public static final ESImpClassRegister<QuestCondition> REGISTRY = new ESImpClassRegister();

	protected boolean asPrecondition = false;

	public void asPrecondition(boolean pre) {
		this.asPrecondition = pre;
	}

	/** 检查条件是否满足 */
	public boolean onCheck(Quest quest, EntityLivingBase player) {
		if (player instanceof EntityPlayer) return this.check(quest, (EntityPlayer) player);
		return false;
	}

	/** 触发时，条件进行记录，记录信息 */
	public <T> void onTrigger(Quest quest, EntityLivingBase player, QuestTrigger<T> type, T data) {

	}

	/** 条件测试成功之后运行，设置对应的数据或者取走物品等操作 */
	public void onFinish(Quest quest, EntityLivingBase player) {
		if (player instanceof EntityPlayer) this.finish(quest, (EntityPlayer) player);
	}

	/**
	 * 获取条件的描述
	 * 
	 * @param dynamic 是否要动态的显示数据，提示任务进度
	 */
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest task, @Nullable EntityLivingBase player, boolean dynamic) {
		return "";
	}

	public boolean check(Quest task, EntityPlayer player) {
		return false;
	}

	public void finish(Quest task, EntityPlayer player) {

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
