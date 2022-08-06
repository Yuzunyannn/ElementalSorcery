package yuzunyannn.elementalsorcery.elf.quest.condition;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.QuestTrigger;
import yuzunyannn.elementalsorcery.elf.quest.loader.QuestCreateFailException;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class QuestCondition extends ESImpClassRegister.EasyImp<QuestCondition>
		implements INBTSerializable<NBTTagCompound> {

	public static final ESImpClassRegister<QuestCondition> REGISTRY = new ESImpClassRegister();

	protected boolean isPrecondition;

	public void asPrecondition(boolean yes) {
		isPrecondition = yes;
	}

	public void initWithConfig(JsonObject json, Map<String, Object> context) {
		throw new QuestCreateFailException(this.getRegistryName() + " is not finish [initWithConfig] function");
	}

	/** 当需要进行数据同步 */
	public void onCheckDataSync(Quest quest, EntityLivingBase player, NBTTagCompound sendData) {

	}

	/** 接受到同步数据 */
	@SideOnly(Side.CLIENT)
	public void onRecvDataSync(Quest quest, EntityLivingBase player, NBTTagCompound sendData) {

	}

	/** 检查条件是否满足 */
	public boolean onCheck(Quest quest, EntityLivingBase player) {
		if (player instanceof EntityPlayer) return this.check(quest, (EntityPlayer) player);
		return true; // 非玩家全满足
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
