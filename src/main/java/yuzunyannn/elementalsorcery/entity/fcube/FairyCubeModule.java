package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESImpClassRegister;

public class FairyCubeModule extends ESImpClassRegister.EasyImp<FairyCubeModule>
		implements INBTSerializable<NBTTagCompound> {

	public static final ESImpClassRegister<FairyCubeModule> REGISTRY = new ESImpClassRegister();

	// 构造数据
	private float priority;
	private int statusCount;
	private ElementStack expExample = ElementStack.EMPTY;
	private short onceExpGetMax = 1;
	public final EntityFairyCube fairyCube;

	// 动态数据
	private float level;
	private int status;

	public FairyCubeModule(EntityFairyCube fairyCube) {
		this.fairyCube = fairyCube;
		statusCount = 1;
		status = 1;
	}

	public int getLimitLevel() {
		return Math.min(fairyCube.getCubeLevel(), MathHelper.floor(this.getLevel()));
	}

	public float getLevel() {
		return level;
	}

	public void setLevel(float level) {
		this.level = level;
	}

	/** 构造函数使用 */
	protected void setStatusCount(int count) {
		statusCount = count;
	}

	/** 获取种类 */
	public int getCurrStatus() {
		return status;
	}

	public int getNextStatus() {
		if (status >= statusCount) return 0;
		return status + 1;
	}

	public void changeToNextStatus() {
		status = this.getNextStatus();
	}

	public void setStatus(int status) {
		this.status = status;
		if (this.status > this.statusCount) this.status = 0;
	}

	public FairyCubeModule setElementNeedPerExp(ElementStack exp, int onceExpGetMax) {
		this.expExample = exp;
		this.onceExpGetMax = (short) onceExpGetMax;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public int getStatusColor(int status) {
		if (status <= 0) return 0x000000;
		switch (status) {
		case 1:
			return 0x1ed68b;
		case 2:
			return 0xe7ff20;
		case 3:
			return 0xee372e;
		case 4:
			return 0xbb23e1;
		case 5:
			return 0x2f2ffd;
		case 6:
			return 0x2ff6fd;
		default:
			break;
		}
		return 0x1ed68b;
	}

	public String getStatusUnlocalizedValue(int status) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	public String getStatusValue(int status) {
		String value = this.getStatusUnlocalizedValue(status);
		return value == null ? null : I18n.format(value);
	}

	/** 当一个模块被安装，只在被安装的第一次调用 */
	public void onInstall(NBTTagCompound installData) {

	}

	/** 当一个模块数据回复时调用 */
	public void onLoaded() {

	}

	/**
	 * 返回优先级 <br/>
	 * 对于属性修改来说：0+是操作层的 100+是直接数值修改层 200+是百分比修改层 300+是数值决定层<br/>
	 * 
	 */
	public float getPriority() {
		return priority;
	}

	/** 吸收元素，如果吸收了返回true */
	public boolean absorbElements(IElementInventory einv) {
		if (expExample.isEmpty()) return false;
		int max = Math.max(1, fairyCube.getRNG().nextInt(onceExpGetMax / 2) + onceExpGetMax / 2);
		ElementStack example = expExample.copy();

		while (true) {
			int count = max * expExample.getCount();
			example.setCount(count);
			ElementStack get = einv.extractElement(example, false);
			if (!get.isEmpty()) {
				fairyCube.absorbColors.add(get.getColor());
				this.addExp(max);
				return true;
			}
			max = max / 2;
			if (max == 0) break;
		}
		return false;
	}

	/** 修改属性，其他的模块来获取某些自定义属性 */
	public float modifyAttribute(String attribute, float value) {
		return value;
	}

	/** 模块的更新 */
	public void onUpdate(EntityLivingBase master, IFairyCubeMaster fairyCubeMaster) {

	}

	/** 当执行，执行者只能有一个，执行期间不在调用onUpdate */
	public void onExecute(EntityLivingBase master, int executeRemain) {

	}

	/** onExecute前调用一次 */
	public void onStartExecute(EntityLivingBase master) {

	}

	/** 如果体力不够，执行不了调用这个 */
	public void onFailExecute() {

	}

	/** 每一次执行获取体力消耗，如果不够，则失败 */
	public float getPhysicalStrengthConsume() {
		return fairyCube.getRNG().nextFloat() * 0.2f + 0.05f;
	}

	/** 获取执行一次的经验，给予精灵立方体 */
	public float getExecuteExpForFairyCube() {
		return level / 2 + 1;
	}

	/** 是否为关闭状态，是的话，不再进行update，但如果处于execute会执行完 */
	public boolean isClose() {
		return status <= 0;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("id", this.getRegistryName().toString());
		nbt.setFloat("level", level);
		nbt.setByte("status", (byte) status);
		return nbt;
	}

	public float getLevelUpgradeProgress() {
		return level - MathHelper.floor(level);
	}

	public void addExp(float count) {
		while (true) {
			int lev = Math.max(MathHelper.ceil(this.getLevel()), 1);
			float decay = (float) Math.pow(lev, 1.75f) * 16;
			float need = 1 - this.getLevelUpgradeProgress();
			if (count / decay < need) {
				level += count / decay;
				return;
			}
			count = count - need * decay;
			level += need;
		}
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.level = nbt.getFloat("level");
		this.status = nbt.getInteger("status");
	}

	public FairyCubeModule setPriority(float priority) {
		this.priority = priority;
		return this;
	}

	public FairyCubeModule setPriority(PriorityType type) {
		switch (type) {
		case EXECUTER:
			this.setPriority(0);
			break;
		case MODIFY_ADD:
			this.setPriority(100);
			break;
		case MODIFY_MULTI:
			this.setPriority(200);
			break;
		}
		return this;
	}

	public static enum PriorityType {
		EXECUTER,
		MODIFY_ADD,
		MODIFY_MULTI,

	};

	@SideOnly(Side.CLIENT)
	public void onRenderGUIIcon() {

	}

}