package yuzunyannn.elementalsorcery.tile.altar;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IMagicBeamHandler;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.ElementTransition;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockCrystalBlock;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockSendRecv;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class TileElementReactor extends TileEntityNetwork implements ITickable, IMagicBeamHandler {

	/** 不稳定片元的默认容量 */
	static public final double INSTABLE_FRAGMENT_BASE_CAPACITY = 1000000;
	/** 每次取出元素的比例 */
	static public final double ONCE_EXTRACT_RATIO = 0.5;
	/** 每次放入元素的比例 */
	static public final double ONCE_INSERT_RATIO = 0.5;
	/** 能连线的底数 POWER_LINE_COEFFICIENT^n */
	static public final double POWER_LINE_COEFFICIENT = 1.2;
	/** 不稳定片元达到容量的某个百分比后，停止能连线的上升 */
	static public final double MAX_IF_RATIO_OF_UPPER_POWER_LINE = 0.01;
	/** 默认运行消耗片元个数基数，实际消耗 基数*能量线 */
	static public final double WORKING_COST_MAGIC_FRAGMENT_BASE = 64;

	protected ReactorStatus status = ReactorStatus.STANDBY;
	protected ElementTransitionReactor core = new ElementTransitionReactor();
	protected int runTick = 0;
	protected double instableRatio = 1;
	protected double instableFragment = 0;
	protected int powerLevelLine = Integer.MAX_VALUE;

	/** 记录上次的状态，用于数据同步 */
	public Element lastReactorElement;

	public static enum ReactorInstableSection {
		ON_RUNAWAY(0.98, 0.001, 512),
		NO_IELEMENT_INVENTORY(0.9995, 0.0001, 8),
		INVENTORY_NO_CHANGE(0.9998, 0.00001, 4),
		DIFF_ELEMENT(0.996, 0.0001, 32);

		public final double chaos;
		public final double ifRatio;
		public final double ifDesc;

		ReactorInstableSection(double chaos, double ifRatio, double ifDesc) {
			this.chaos = MathHelper.clamp(chaos, Double.MIN_VALUE, 1);
			this.ifRatio = ifRatio;
			this.ifDesc = ifDesc;
		}

		public double doChaos(double factor) {
			return Math.pow(chaos, (1 + Math.min(Math.abs(factor), 0.5)));
		}

		public double descIFCount(double originCount) {
			return this.ifDesc + originCount * this.ifRatio;
		}
	}

	public static enum ReactorStatus {
		OFF(false),
		STANDBY(false),
		RUNNING(true),
		RUNAWAY(true),
		CLOSING(true);

		public final boolean isRunning;

		ReactorStatus(boolean isRun) {
			this.isRunning = isRun;
		}
	}

	public ElementTransitionReactor getReactorCore() {
		return core;
	}

	public ReactorStatus getStatus() {
		return status == null ? ReactorStatus.OFF : status;
	}

	@SideOnly(Side.CLIENT)
	public void setStatus(ReactorStatus status) {
		this.status = status;
	}

	/**
	 * 获取当前能量线<br/>
	 * 能连线为POWER_LINE_COEFFICIENT的指数<br/>
	 * 即ceil(POWER_LINE_COEFFICIENT^powerLevelLine)为当前输出元素的能量值
	 */
	public int getPowerLine() {
		return powerLevelLine;
	}

	/** 不稳定率∈(0,1]，越大越稳定 */
	public double getInstableRatio() {
		return instableRatio;
	}

	/** 设置不稳定率 */
	public void setInstableRatio(double instableRatio) {
		this.instableRatio = MathHelper.clamp(instableRatio, Double.MIN_VALUE, 1);
	}

	/** 获取不稳点片元的最大储存量，这个量个动态变化的，随着不稳定率的减少增加 */
	public double getInstableFragmentCapacity() {
		return INSTABLE_FRAGMENT_BASE_CAPACITY / MathHelper.sqrt(getInstableRatio());
	}

	public double getInstableFragment() {
		return instableFragment;
	}

	public void setInstableFragment(double instableFragment) {
		this.instableFragment = instableFragment;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound nbt = super.writeToNBT(compound);
		nbt.setByte("status", (byte) status.ordinal());
		nbt.setInteger("rTick", runTick);
		nbt.setFloat("iR", (float) instableRatio);
		nbt.setDouble("iF", instableFragment);
		nbt.setInteger("pLine", powerLevelLine);
		return core.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		runTick = compound.getInteger("rTick");
		status = ReactorStatus.values()[compound.getByte("status")];
		instableRatio = compound.getDouble("iR");
		instableFragment = compound.getDouble("iF");
		powerLevelLine = compound.getInteger("pLine");
		core.readFromNBT(compound);
	}

	/** 获取当前元素片元的转化到不稳定魔力片元的比例 */
	public double getInstableFragmentRatio() {
		return Math.pow(7000, 1 / Math.pow(getInstableRatio(), 0.1) - 1);
	}

	/**
	 * 消耗元素片元，增长不稳定片元
	 * 
	 * @param count 消耗几个当前的元素片元
	 */
	public void growInstableFragmentFromCore(double count) {
		double ratio = getInstableFragmentRatio();
		double fragment = core.shrink(count);
		ElementTransition et = core.getElement().getTransition();
		if (et != null) fragment = ElementHelper.transitionFrom(core.getElement(), fragment, et.getLevel());
		this.instableFragment = this.instableFragment + fragment * ratio;
	}

	/**
	 * 将不稳定片元，转会元素片元
	 */
	public void pushBackInstableFragmentToCore(double count) {
		count = Math.min(this.instableFragment, count);
		this.instableFragment = this.instableFragment - count;
		double fragment = count;
		ElementTransition et = core.getElement().getTransition();
		if (et != null) fragment = ElementHelper.transitionTo(core.getElement(), fragment, et.getLevel());
		core.insert(core.getElement(), fragment);
	}

	/**
	 * 启动反应堆
	 * 
	 * @return 启动状态
	 */
	public boolean launch() {
		if (world.isRemote) return false;
		if (status.isRunning) return true;
		if (status != ReactorStatus.STANDBY) return false;
		status = ReactorStatus.RUNNING;
		instableRatio = 1;
		instableFragment = 1;
		runTick = 0;
		core.reset();
		updateToClient();
		markDirty();
		return true;
	}

	public boolean close() {
		if (world.isRemote) return false;
		if (!status.isRunning) return false;
		if (status == ReactorStatus.RUNAWAY) return false;
		if (status == ReactorStatus.CLOSING) return true;
		status = ReactorStatus.CLOSING;
		updateToClient();
		markDirty();
		return true;
	}

	/**
	 * 进行不稳定操作
	 * 
	 * @param reason 不稳定的原因
	 * @param factor 程度0~1
	 */
	public void instable(ReactorInstableSection reason, double factor) {
		ReactorStatus status = getStatus();
		if (!status.isRunning) return;
		if (core.getFragment() < 0.001) return;
		setInstableRatio(Math.max(getInstableRatio() * reason.doChaos(factor), 0.0001));
		growInstableFragmentFromCore(reason.descIFCount(core.getFragment()));
	}

	/** 不稳定片元的每次的最大传输率，因为可以放置4个塔，所以实际最快是单个的4倍 */
	public double getInstableFragmentMaxTransmissionCount() {
		double capacity = getInstableFragmentCapacity();
		return capacity / 160 * Math.pow(powerLevelLine / 10.0 + 1, 1.5);
	}

	// 接受片元，抵消不稳定片元，重新变成当前元素的元素片元，并提升稳定度
	@Override
	public double insertMagicFragment(double getCount, boolean simulate) {
		ReactorStatus status = getStatus();
		if (status != ReactorStatus.RUNNING) return getCount;
		double mustCost = WORKING_COST_MAGIC_FRAGMENT_BASE * powerLevelLine;
		if (getCount < mustCost) return getCount;

		double instableFragment = getInstableFragment();
		if (instableFragment == 0) {
			if (getInstableRatio() < 1) {
				setInstableRatio(getInstableRatio() * 1.0005f);
				return getCount - mustCost * 2;
			}
			return getCount - mustCost;
		}

		double insertCount = Math.min(getCount - mustCost, getInstableFragmentMaxTransmissionCount());
		double ratio = getInstableFragmentRatio() * 2;
		double costCount = Math.min(instableFragment * ratio, insertCount);
		pushBackInstableFragmentToCore(costCount / ratio);
		return getCount - costCount - mustCost;
	}

	@Override
	public double extractMagicFragment(double needCount, boolean simulate) {
		ReactorStatus status = getStatus();
		if (!status.isRunning) return 0;
		if (status == ReactorStatus.RUNAWAY) return 0;
		double capacity = getInstableFragmentCapacity();
		double instableFragment = getInstableFragment();
		double minRatio = Math.max(MAX_IF_RATIO_OF_UPPER_POWER_LINE + 0.01, getInstableRatio() * 0.75f);
		// 如果没到不稳定魔力片元存储的输出比例，不会进行输出
		if (instableFragment / capacity < minRatio && getStatus() != ReactorStatus.CLOSING) return 0;
		// 进行输出多余的魔力片元
		double extractCount = Math.min(needCount, getInstableFragmentMaxTransmissionCount());
		extractCount = Math.min(extractCount, this.instableFragment);
		setInstableFragment(instableFragment - extractCount);
		return extractCount;
	}

	protected void tryLinkOrUnlinkMagicIR(boolean isLink) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			TileEntity tile = world.getTileEntity(pos.offset(facing, 8));
			if (tile instanceof TileIceRockCrystalBlock) {
				((TileIceRockSendRecv) tile).setBeamHandler(facing.getOpposite(), isLink ? this : null);
			}
		}
	}

	protected TileEntity getElementTile(int index) {
		float alpha = index * 3.1415926f / 4 + 3.1415926f / 8;
		int x = (int) (MathHelper.cos(alpha) / 0.99f * 6f);
		int z = (int) (MathHelper.sin(alpha) / 0.99f * 6f);
		return world.getTileEntity(pos.add(x, 0, z));
	}

	@Override
	public void update() {
		ReactorStatus status = getStatus();
		if (!status.isRunning) return;
		if (world.isRemote) updateClientEffect();
		runTick++;
		if (runTick % 10 == 0) {
			updateBlast();
			updateByIndex(runTick / 10 - 1);
			// 正常情况，每次消耗两点片元，失控下消耗大量
			if (status == ReactorStatus.RUNAWAY) instable(ReactorInstableSection.ON_RUNAWAY, 1);
			else growInstableFragmentFromCore(2);
			if (world.isRemote) return;
			if (lastReactorElement != core.getElement()) {
				lastReactorElement = core.getElement();
				this.updateToClient();
			}
		}
		if (runTick % 20 == 0) tryLinkOrUnlinkMagicIR(status != ReactorStatus.RUNAWAY);
	}

	public void updateBlast() {
		double instableFragment = getInstableFragment();
		double instableFragmentRatio = instableFragment / getInstableFragmentCapacity();
		if (instableFragmentRatio < 1) return;
		if (world.rand.nextDouble() < 0.8 / instableFragmentRatio) return;
		double useInstableFragment = instableFragment / 2;
		setInstableFragment(instableFragment - useInstableFragment);
		doBlast(useInstableFragment);
	}

	public void onBreak() {
		ReactorStatus status = getStatus();
		if (!status.isRunning) return;
		setInstableRatio(getInstableRatio() * 0.5);
		growInstableFragmentFromCore(core.getFragment());
		doBlast(getInstableFragment());
	}

	public void doBlast(double instableFragment) {
		if (world.isRemote) return;
		// TODO 不稳定魔力片元溢出爆炸
		System.out.println("爆炸！" + instableFragment);
	}

	/** 当关闭 */
	public void onClose() {
		this.status = ReactorStatus.STANDBY;
		tryLinkOrUnlinkMagicIR(false);
		updateToClient();
		markDirty();
	}

	/**
	 * 通过index进行一次更新<br/>
	 * Client 调用进行动画播放，通过runTick同步动画播放位置<br/>
	 * Server 调用处理逻辑<br/>
	 * 
	 */
	public void updateByIndex(int index) {
		TileEntity tile = getElementTile(index);
		// 不是元素容器，直接返回，等待下一个
		IElementInventory eInv = ElementHelper.getElementInventory(tile);
		if (eInv == null) {
			instable(ReactorInstableSection.NO_IELEMENT_INVENTORY, 0);
			return;
		}
		// 不是祭坛形也不可以
		IAltarWake altarWake = null;
		if (tile instanceof IAltarWake) altarWake = (IAltarWake) tile;
		if (altarWake == null) {
			instable(ReactorInstableSection.NO_IELEMENT_INVENTORY, 0);
			return;
		}

		// 好了，根据状态决策，到这里只能有 正在关闭 和 启动状态
		ReactorStatus status = getStatus();
		if (status == ReactorStatus.CLOSING) {
			// close状态
			int flag = updateClosing(eInv);
			if (flag == 0) onClose();
			if (flag == 0 || flag == 1) {
				altarWake.wake(IAltarWake.OBTAIN, pos);
				if (world.isRemote) playChangeEffect(tile.getPos(), 1);
			}
			return;
		}

		// 正式进入running状态
		// 開始决定是获取还是放入，并进行操作
		int changeFlag = updateEIFragment(eInv);
		if (changeFlag == 1) altarWake.wake(IAltarWake.SEND, pos);
		else if (changeFlag == 2) altarWake.wake(IAltarWake.OBTAIN, pos);
		else {
			instable(ReactorInstableSection.INVENTORY_NO_CHANGE, 0);
			return;
		}

		if (world.isRemote) {
			playChangeEffect(tile.getPos(), changeFlag);
			return;
		}

		Element toElement = core.getSuggestTransition();
		if (toElement != null && toElement != core.getElement()) {
			core.transitTo(toElement);
			double transitLoss = Math.pow((1 - getInstableRatio()) * 0.6, 8) * 0.5 * core.getFragment();
			if (transitLoss > 1) growInstableFragmentFromCore(transitLoss);
		}

		markDirty();
	}

	/**
	 * 根据line将内部存储的片元转化到指定仓库里
	 * 
	 * @retun 失败原因 0成功 1片元不够 2容器满了 3拒绝插入
	 */
	protected int transferToElementInventory(IElementInventory eInv, int i, int currPowerLine, double insertRatio) {
		ElementStack eStack = eInv.getStackInSlot(i);
		int power = MathHelper.ceil(Math.pow(POWER_LINE_COEFFICIENT, currPowerLine));
		int maxSize = eInv.getMaxSizeInSlot(i);
		if (maxSize > 0) {
			// 有容量上限的情况下，计算还能插入多少
			maxSize = maxSize - eStack.getCount();
			maxSize *= ONCE_INSERT_RATIO;
			if (maxSize <= 0) return 2;
		}
		ElementStack insert = core.extract(insertRatio, maxSize, power, true);
		if (insert.isEmpty()) return 1;
		if (!eInv.insertElement(i, insert, false)) return 3;
		core.extract(insertRatio, maxSize, power, false);
		return 0;
	}

	public final List<Element> echoElementContentList = new ArrayList<>();

	// 跟新一次仓库，可能是获取也可能是放入
	protected int updateEIFragment(IElementInventory eInv) {
		int changeFlag = 0;
		echoElementContentList.clear();
		for (int i = 0; i < eInv.getSlots(); i++) {
			int currPowerLine = this.powerLevelLine;

			ElementStack eStack = eInv.getStackInSlot(i);
			boolean isInsert = false;
			if (eStack.isEmpty()) isInsert = true;
			else {
				echoElementContentList.add(eStack.getElement());
				int powerLine = (int) (Math.log(eStack.getPower()) / Math.log(POWER_LINE_COEFFICIENT) + 0.01f);
				if (core.getElement() == ElementStack.EMPTY.getElement()) this.powerLevelLine = powerLine;
				else {
					double instableFragmentRatio = getInstableFragment() / getInstableFragmentCapacity();
					this.powerLevelLine = Math.min(powerLine, this.powerLevelLine);
					// 不稳片元率存储必须小于1%(MAX_IF_RATIO_OF_UPPER_POWER_LINE)才能进阶
					if (instableFragmentRatio < MAX_IF_RATIO_OF_UPPER_POWER_LINE)
						this.powerLevelLine = this.powerLevelLine + 1;
					isInsert = eStack.getPower() == MathHelper.ceil(Math.pow(POWER_LINE_COEFFICIENT, currPowerLine));
				}
			}

			if (isInsert) insert: {
				if (changeFlag == 1) break insert;
				if (transferToElementInventory(eInv, i, currPowerLine, ONCE_INSERT_RATIO) != 0) break insert;
				changeFlag = 2;
				continue;
			}
			// 没有插入成功，就尝试取
			if (changeFlag == 2) continue;
			if (eStack.isEmpty()) continue;
			ElementStack extract = eStack.copy();
			extract.setCount(MathHelper.ceil(extract.getCount() * ONCE_EXTRACT_RATIO));
			ElementStack getStack = eInv.extractElement(i, extract, false);
			if (getStack.isEmpty()) continue;
			changeFlag = 1;
			core.insert(getStack.getElement(), ElementHelper.toFragment(getStack));
			if (core.lastElementDiff) {
				double f = Math.abs(core.lastDetaAngle / 180) + Math.abs(core.lastDetaStep);
				this.instable(ReactorInstableSection.DIFF_ELEMENT, f);
			}
		}

		return changeFlag;
	}

	protected int updateClosing(IElementInventory eInv) {
		boolean isInster = false;
		for (int i = 0; i < eInv.getSlots(); i++) {
			int flag = transferToElementInventory(eInv, i, this.powerLevelLine, 1);
			if (flag == 1 || this.powerLevelLine == 1) return 0;
			if (flag == 1) this.powerLevelLine = Math.max(this.powerLevelLine - 1, 1);
			isInster = isInster || flag == 0;
		}
		return isInster ? 1 : 2;
	}

	@SideOnly(Side.CLIENT)
	protected Color renderColor;
	// 是否被渲染了
	@SideOnly(Side.CLIENT)
	public boolean isInRender;

	@SideOnly(Side.CLIENT)
	public Color getRenderColor() {
		if (renderColor != null) return renderColor;
		return renderColor = new Color(0x4d2175);
	}

	@SideOnly(Side.CLIENT)
	public void playChangeEffect(BlockPos to, int changeFlag) {
		Vec3d p1 = new Vec3d(to).add(0.5, 0.5, 0.5);
		Vec3d p2 = new Vec3d(pos).add(0.5, 0.5, 0.5);
		Vec3d dir = p1.subtract(p2).scale(1 / 32f);
		for (int i = 0; i < 32; i++) {
			Vec3d at = p2.add(dir.scale(i));
			EffectFragmentMove effect = new EffectFragmentMove(world, at);
			effect.prevScale = effect.scale = effect.defaultScale = 0.02f + Effect.rand.nextFloat() * 0.08f;
			Color color = getRenderColor();
			if (echoElementContentList.size() > 0) {
				int index = Effect.rand.nextInt(echoElementContentList.size() + 1);
				if (index < echoElementContentList.size()) {
					Element element = echoElementContentList.get(index);
					color = new Color(element.getColor(new ElementStack(element)));
				}
			}
			effect.color.setColor(color);
			Effect.addEffect(effect);
			Vec3d move = new Vec3d(Effect.rand.nextDouble() - 0.5, Effect.rand.nextDouble() - 0.5,
					Effect.rand.nextDouble() - 0.5).scale(0.07);
			effect.motionX = move.x;
			effect.motionY = move.y;
			effect.motionZ = move.z;
			move = move.scale(0.05);
			effect.xAccelerate = -move.x;
			effect.yAccelerate = -move.y;
			effect.zAccelerate = -move.z;
		}

	}

	@SideOnly(Side.CLIENT)
	public void updateClientEffect() {
		if (!isInRender) return;
		isInRender = false;
		if (status == ReactorStatus.CLOSING) return;
		if (core.getElement() == ElementStack.EMPTY.getElement()) return;
		Vec3d at = new Vec3d(pos).add(0.5, 0.5, 0.5);
		EffectFragmentMove effect = new EffectFragmentMove(world, at);
		effect.prevScale = effect.scale = effect.defaultScale = 0.01f + Effect.rand.nextFloat() * 0.02f;
		effect.color.setColor(getRenderColor());
		Vec3d move = new Vec3d(Effect.rand.nextDouble() - 0.5, Effect.rand.nextDouble() - 0.5,
				Effect.rand.nextDouble() - 0.5).scale(0.07);
		effect.motionX = move.x;
		effect.motionY = move.y;
		effect.motionZ = move.z;
		move = move.scale(0.05);
		effect.xAccelerate = -move.x;
		effect.yAccelerate = -move.y;
		effect.zAccelerate = -move.z;
		Effect.addEffect(effect);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		Element element = core.getElement();
		if (element == ElementStack.EMPTY.getElement()) renderColor = null;
		else renderColor = new Color(element.getColor(new ElementStack(element)));
		lastReactorElement = element;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().grow(0.25);
	}
}
