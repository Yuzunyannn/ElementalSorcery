package yuzunyannn.elementalsorcery.tile.altar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IMagicBeamHandler;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.ElementTransition;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.grimoire.remote.IFragmentMantraLauncher;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.render.effect.scrappy.EffectReactorMantraSpell;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockCrystalBlock;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockSendRecv;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockStand;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.world.MapHelper;
import yuzunyannn.elementalsorcery.util.world.WorldLocation;

public class TileElementReactor extends TileEntityNetwork implements ITickable, IMagicBeamHandler {

	static public final int SELECT_MAP_SIZE = 32;

	/** 不稳定片元的默认容量 */
	static public final double INSTABLE_FRAGMENT_BASE_CAPACITY = 1000000;
	/** 每次取出元素的比例 */
	static public final double ONCE_EXTRACT_RATIO = 0.5;
	/** 在释放咒文的时候，每次取出来的量 */
	static public final double ONCE_EXTRACT_RATIO_IN_MANTRA = 0.01;
	/** 每次放入元素的比例 */
	static public final double ONCE_INSERT_RATIO = 0.5;
	/** 能连线的底数 POWER_LINE_COEFFICIENT^n */
	static public final double POWER_LINE_COEFFICIENT = 1.2;
	/** 不稳定片元达到容量的某个百分比后，停止能连线的上升 */
	static public final double MAX_IF_RATIO_OF_UPPER_POWER_LINE = 0.01;
	/** 默认运行消耗片元个数基数，实际消耗 基数*能量线 */
	static public final double WORKING_COST_MAGIC_FRAGMENT_BASE = 64;

	protected ReactorStatus status = ReactorStatus.STANDBY;
	protected ElementTransitionReactor core = new ElementTransitionReactor(this);
	protected int runTick = 0;
	protected double instableRatio = 1;
	protected double instableFragment = 0;
	protected int powerLevelLine = Integer.MAX_VALUE;
	// 反应堆远程魔法部分
	protected List<Mantra> mantras = new ArrayList<>();
	// 地图显示数据记录
	public boolean hasInConatinerMark;
	// 地图部分
	@Nonnull
	protected WorldLocation mapLocation = new WorldLocation(DimensionType.OVERWORLD.getId(), BlockPos.ORIGIN);
	protected WorldLocation targetLocation;
	protected MapHelper worldMap;
	// 运行咒文部分
	protected IFragmentMantraLauncher.MLPair runningMantraPair;
	protected VariableSet mantraContent = new VariableSet();
	// 表明是否充能完成，等待发射的状态
	public boolean isChargeFinMark;
	// 发射延迟，优先播动画
	protected IFragmentMantraLauncher.MLPair waitSpellLauncher;
	public int waitSpellTick;
	// 充能进度，实时计算
	public float mantraChargeProgress;

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

	public TileElementReactor() {
		addMantra(ESInit.MANTRAS.ENDER_TELEPORT);
		addMantra(ESInit.MANTRAS.FIRE_BALL);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		worldMap = new MapHelper(this.pos, SELECT_MAP_SIZE);
	}

	public void addMantra(Mantra mantra) {
		List<IFragmentMantraLauncher> list = mantra.getFragmentMantraLaunchers();
		if (list == null) return;
		if (list.isEmpty()) return;
		if (mantras.size() >= 4) return;
		if (mantras.indexOf(mantra) != -1) return;
		mantras.add(mantra);
	}

	public void updateMapLocation(WorldLocation location, boolean force) {
		if (world.isRemote) return;
		if (location == null) location = new WorldLocation(world, pos);
		if (location.getPos() == null) return;
		if (location.equals(this.mapLocation) && !force) return;
		this.mapLocation = location;
		worldMap.setPos(this.mapLocation.getPos());
		this.worldMap.detectBlock(this.mapLocation.getWorldMust(world));
	}

	public boolean launchTargetLocation(BlockPos base, BlockPos offset) {
		if (!isRunningMantra()) return false;
		if (this.mapLocation == null) return false;
		if (!this.mapLocation.getPos().equals(base)) return false;
		if (waitSpellLauncher != null) return false;
		World world = this.mapLocation.getWorldMust(this.world);
		BlockPos pos = new BlockPos(base.getX() + offset.getX(), 255, base.getZ() + offset.getZ());
		if (worldMap.getYOffset(offset.getX(), offset.getZ()) != -1)
			pos = new BlockPos(pos.getX(), worldMap.getYOffset(offset.getX(), offset.getZ()), pos.getZ());
		else while (world.isAirBlock(pos) && pos.getY() > 0) pos = pos.down();
		setTargetLocation(new WorldLocation(world, new BlockPos(pos)));
		return true;
	}

	public void setTargetLocation(WorldLocation targetLocation) {
		this.targetLocation = targetLocation;
	}

	public List<Mantra> getMantras() {
		return mantras;
	}

	public MapHelper getWorldMap() {
		return worldMap;
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
		if (!isSending()) {
			nbt.setTag("mas", NBTHelper.serializeMantra(mantras));
		}
		if (runningMantraPair != null) {
			nbt.setString("mlId", runningMantraPair.toId());
			nbt.setTag("mac", mantraContent.serializeNBT());
		}
		return core.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		runTick = nbt.getInteger("rTick");
		status = ReactorStatus.values()[nbt.getByte("status")];
		instableRatio = nbt.getDouble("iR");
		instableFragment = nbt.getDouble("iF");
		powerLevelLine = nbt.getInteger("pLine");
		core.readFromNBT(nbt);
		if (nbt.hasKey("mas", NBTTag.TAG_LIST))
			mantras = NBTHelper.deserializeMantra(nbt.getTagList("mas", NBTTag.TAG_STRING));
		if (nbt.hasKey("mlId")) {
			runningMantraPair = IFragmentMantraLauncher.fromId(nbt.getString("mlId"));
			mantraContent.deserializeNBT(nbt.getCompoundTag("mac"));
		} else runningMantraPair = null;
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
		updateMapLocation(null, false);
		powerLevelLine = 16;
		status = ReactorStatus.RUNNING;
		instableRatio = 1;
		instableFragment = 1;
		runTick = 0;
		core.reset();
		updateToClient();
		markDirty();
		return true;
	}

	/**
	 * 关机
	 */
	public boolean shutdown() {
		if (world.isRemote) return false;
		if (!status.isRunning) return false;
		if (status == ReactorStatus.RUNAWAY) return false;
		if (status == ReactorStatus.CLOSING) return true;
		status = ReactorStatus.CLOSING;
		updateToClient();
		markDirty();
		return true;
	}

	public boolean launchMantra(IFragmentMantraLauncher.MLPair pair) {
		if (runningMantraPair != null) return true;
		if (pair == null) return false;
		if (!pair.launcher.canUse(core)) return false;
		updateMapLocation(mapLocation, true);
		runningMantraPair = pair;
		mantraContent = new VariableSet();
		targetLocation = null;
		waitSpellLauncher = null;
		isChargeFinMark = false;
		this.markDirty();
		this.updateToClient();
		return true;
	}

	public void shutdownMantra() {
		if (runningMantraPair == null) return;
		runningMantraPair = null;
		this.updateToClient();
		this.markDirty();
	}

	public IFragmentMantraLauncher.MLPair getRunningMantraPair() {
		return runningMantraPair;
	}

	public boolean isRunningMantra() {
		return this.runningMantraPair != null && getStatus() == ReactorStatus.RUNNING;
	}

	public boolean isRunningMantraReady() {
		return isRunningMantra() && isChargeFinMark;
	}

	public boolean tryShiftChargeStatus(boolean isFin) {
		if (!isRunningMantra()) return false;
		if (isChargeFinMark) {
			if (isFin) return true;
			if (!canContinueCharge()) return false;
			isChargeFinMark = isFin;
		} else {
			if (!isFin) return true;
			if (mantraChargeProgress < getRunningMantraPair().launcher.getMinCanCastCharge(world, core, mantraContent))
				return false;
			isChargeFinMark = isFin;
		}
		return true;
	}

	public boolean canContinueCharge() {
		return mantraChargeProgress < 1;
	}

	public boolean canReactor() {
		return !isRunningMantra() || getRunningMantraPair().launcher.needContinueReact(world, core, mantraContent);
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
				TileIceRockSendRecv ircb = ((TileIceRockSendRecv) tile);
				TileIceRockStand core = ircb.getIceRockCore();
				if (core != null && core.getLinkCount() >= 3 && isLink) ircb.setBeamHandler(facing.getOpposite(), this);
				else ircb.setBeamHandler(facing.getOpposite(), null);
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
		if (runTick % 10 == 0) tryHandle: {
			if (!canReactor()) break tryHandle;
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
		// 咒文
		if (isRunningMantra()) {
			IFragmentMantraLauncher launcher = getRunningMantraPair().launcher;
			if (!isChargeFinMark) {
				mantraChargeProgress = launcher.charging(world, core, mantraContent);
				if (!canContinueCharge()) isChargeFinMark = true;
				waitSpellLauncher = null;
			} else if (!world.isRemote) {
				if (targetLocation != null && waitSpellLauncher != getRunningMantraPair()) {
					// 放招延迟1s
					waitSpellLauncher = getRunningMantraPair();
					waitSpellTick = 10;
					// 向所有满足条件的人发送视图
					sendLauncherEffect();
				}
			}
		}
		if (world.isRemote) {
			if (waitSpellTick > 0) waitSpellTick--;
			return;
		}
		// 等待施法进行释放
		if (waitSpellLauncher != null) {
			if (waitSpellTick-- <= 0) {
				waitSpellLauncher.launcher.cast(world, pos, targetLocation, mantraContent);
				waitSpellLauncher = null;
				shutdownMantra();
			}
		}
		// 其他更新
		if (runTick % 20 == 0) {
			if (isRunningMantra()) if (mapLocation.getPos() == BlockPos.ORIGIN) updateMapLocation(null, false);
			tryLinkOrUnlinkMagicIR(status != ReactorStatus.RUNAWAY);
			if (hasInConatinerMark) {
				hasInConatinerMark = false;
				WorldServer ret = net.minecraftforge.common.DimensionManager.getWorld(mapLocation.getDimension(), true);
				if (ret != null) worldMap.detectEntity(mapLocation.getWorldMust(world));
			}
		}
	}

	public void sendLauncherEffect() {
		int dimId = mapLocation.getDimension();
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimId", dimId);
		nbt.setString("lId", waitSpellLauncher.toId());

		MinecraftServer server = ((WorldServer) world).getMinecraftServer();
		PlayerList players = server.getPlayerList();
		float viewDis = server.getPlayerList().getViewDistance() * 16;
		for (EntityPlayer player : players.getPlayers()) {
			if (player.world.provider.getDimension() != dimId) continue;
			if (player.getDistanceSq(targetLocation.getPos()) > viewDis * viewDis) continue;
			Effects.spawnEffect(player, Effects.REACTOR_MANREA, new Vec3d(targetLocation.getPos()).add(0.5, 1, 0.5),
					nbt);
		}
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
		ReactorStatus status = getStatus();
		TileEntity tile = getElementTile(index);
		// 不是元素容器，直接返回，等待下一个
		IElementInventory eInv = ElementHelper.getElementInventory(tile);
		if (eInv == null) {
			// 如果没有仓库，也需要进行关闭处理
			if (status == ReactorStatus.CLOSING) {
				int flag = updateClosing(null);
				if (flag == 0) onClose();
				return;
			}
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

		if (isRunningMantra()) return;

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
			boolean isRunningMantra = isRunningMantra();
			if (isRunningMantra) {
				isInsert = false;
				echoElementContentList.add(eStack.getElement());
			} else if (eStack.isEmpty()) isInsert = true;
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
			if (isRunningMantra) extract.setCount(MathHelper.ceil(extract.getCount() * ONCE_EXTRACT_RATIO_IN_MANTRA));
			else extract.setCount(MathHelper.ceil(extract.getCount() * ONCE_EXTRACT_RATIO));
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
		if (eInv == null) {
			this.powerLevelLine = MathHelper.ceil(this.powerLevelLine * 0.5);
			if (this.powerLevelLine <= 1) return 0;
			return 2;
		}
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
	public EffectReactorMantraSpell effectLink;

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

		if (isRunningMantra()) {
			if (effectLink == null) {
				effectLink = new EffectReactorMantraSpell(world, new Vec3d(pos).add(0.5, 1, 0.5), false);
				effectLink.setMainColor(renderColor);
				effectLink.launcher = getRunningMantraPair().launcher;
				Effect.addEffect(effectLink);
			}
			effectLink.prevProgress = effectLink.progress;
			effectLink.progress = Math.min(mantraChargeProgress, 1);
			effectLink.hold(20);
		} else effectLink = null;

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
