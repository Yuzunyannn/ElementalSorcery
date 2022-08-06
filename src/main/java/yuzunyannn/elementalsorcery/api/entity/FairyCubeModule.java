package yuzunyannn.elementalsorcery.api.entity;

import java.lang.reflect.Method;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.ESImpClassRegister;

public class FairyCubeModule extends ESImpClassRegister.EasyImp<FairyCubeModule>
		implements INBTSerializable<NBTTagCompound> {

	public static final ESImpClassRegister<FairyCubeModule> REGISTRY = new ESImpClassRegister();

	// 构造数据
	private float priority;
	private int statusCount;
	protected ElementStack expExample = ElementStack.EMPTY;
	protected short onceExpGetMax = 2;
	public final IFairyCubeObject fairyCube;

	// 动态数据
	private double level;
	private int status;

	public FairyCubeModule(IFairyCubeObject fairyCube) {
		this.fairyCube = fairyCube;
		statusCount = 1;
		status = 1;
	}

	public double getLuck() {
		double plunder = fairyCube.getAttribute("plunder");
		double fortune = fairyCube.getAttribute("fortune");
		return fairyCube.getAttribute("luck", (plunder + fortune) / 2);
	}

	public int getLevelUsed() {
		return Math.min(fairyCube.getCubeLevel(), MathHelper.floor(this.getLevel()));
	}

	public double getLevel() {
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
			if (priority >= 1000) return 0xffaaff;
			if (priority >= 100) return 0x4df3f9;
			return 0x1ed68b;
		case 2:
			if (priority >= 100) return 0x8b58ff;
			return 0xe7ff20;
		case 3:
			if (priority >= 100) return 0xee372e;
			return 0xffae00;
		case 4:
			return 0x874697;
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

	/** 在gui被点击 */
	public void onClickOnGUI(int type, EntityPlayer player) {
		this.changeToNextStatus();
	}

	/** 当一个模块被安装，只在被安装的第一次调用 */
	public void onInstall(NBTTagCompound installData) {

	}

	/** 当一个模块数据回复时调用 */
	public void onLoaded() {

	}

	/**
	 * 返回优先级 <br/>
	 * 对于属性修改来说：0+是操作层的 100+是数值决定层 200+是百分比修改层 300+是直接数值修改层<br/>
	 * 
	 */
	public float getPriority() {
		return priority;
	}

	/** 吸收元素，如果吸收了返回true */
	public boolean absorbElements(IElementInventory einv) {
		if (expExample.isEmpty()) return false;
		int max = Math.max(1, fairyCube.getRNG().nextInt(onceExpGetMax / 2) + onceExpGetMax / 2);
		return absorbElementToExp(expExample, einv, max);
	}

	protected boolean absorbElementToExp(ElementStack expExample, IElementInventory einv, int maxCount) {
		if (expExample.isEmpty()) return false;
		ElementStack example = expExample.copy();
		while (true) {
			int count = maxCount * expExample.getCount();
			example.setCount(count);
			ElementStack get = einv.extractElement(example, false);
			if (!get.isEmpty()) {
				fairyCube.addAbsorbColor(get.getColor());
				this.addExp(maxCount);
				return true;
			}
			maxCount = maxCount / 2;
			if (maxCount == 0) break;
		}
		return false;
	}

	/** 修改属性，其他的模块来获取某些自定义属性 */
	public double modifyAttribute(String attribute, double value) {
		return value;
	}

	/** 模块的tick，只要模块被安装，就会调用 */
	public void onTick(EntityLivingBase master) {

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
	public double getExecuteExpForFairyCube() {
		return level / 2 + 1;
	}

	/** 是否为关闭状态，是的话，不再进行update，但如果处于execute会执行完 */
	public boolean isClose() {
		return status <= 0;
	}

	public void addExp(double count) {
		count = fairyCube.getAttribute("experience:module", count);
		while (true) {
			double decay = this.getExpToNextLevel();
			double need = 1 - this.getLevelUpgradeProgress();
			if (count / decay < need) {
				level += count / decay;
				return;
			}
			count = count - need * decay;
			level += need + 0.00001;
		}
	}

	public double getLevelUpgradeProgress() {
		return level - MathHelper.floor(level);
	}

	/** 获取到下一个等级的经验值 */
	public double getExpToNextLevel() {
		int lev = Math.max(MathHelper.ceil(this.getLevel()), 1);
		double decay = (float) Math.pow(lev, 1.25f) * 24;
		return decay;
	}

	public void sendToClient(NBTTagCompound nbt) {
		fairyCube.sendModuleDataToClient(this, nbt);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.level = nbt.getDouble("level");
		this.status = nbt.getInteger("status");
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("id", this.getRegistryName().toString());
		nbt.setDouble("level", level);
		nbt.setByte("status", (byte) status);
		return nbt;
	}

	@SideOnly(Side.CLIENT)
	public void onRecv(NBTTagCompound nbt) {

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
			this.setPriority(300);
			break;
		case MODIFY_MULTI:
			this.setPriority(200);
			break;
		case CONTAINER:
			this.setPriority(1000);
			break;
		}
		return this;
	}

	public static enum PriorityType {
		EXECUTER,
		MODIFY_ADD,
		MODIFY_MULTI,
		CONTAINER;
	};

	public static boolean tryMatchAndConsumeForCraft(Class<? extends FairyCubeModule> cls, World world, BlockPos pos,
			IElementInventory inv) {
		try {
			Method methods[] = cls.getDeclaredMethods();
			for (Method method : methods) {
				FairyCubeModuleRecipe recipe = method.getAnnotation(FairyCubeModuleRecipe.class);
				if (recipe == null) continue;
				Object ret = method.invoke(cls, world, pos, inv);
				if (ret instanceof Boolean) return (Boolean) ret;
				return ret != null;
			}
		} catch (ReflectiveOperationException e) {
			if (ESAPI.isDevelop) {
				ESAPI.logger.warn("存在模块没完成matchAndConsumeForCraft函数:" + cls.getName());
			}
		}
		return false;
	}

	/** 匹配，如果匹配成功了，消耗元素，返回true，表示合成 */
	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		return false;
	}

}
