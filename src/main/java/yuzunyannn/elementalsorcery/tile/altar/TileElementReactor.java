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
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockCrystalBlock;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockSendRecv;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class TileElementReactor extends TileEntityNetwork implements ITickable, IMagicBeamHandler {

	static public final double INSTABLE_FRAGMENT_BASE_CAPACITY = 1000000;
	static public final double ONCE_EXTRACT_RATIO = 0.5;
	static public final double ONCE_INSERT_RATIO = 0.5;
	static public final double POWER_LINE_COEFFICIENT = 1.2f;

	protected ReactorStatus status = ReactorStatus.STANDBY;
	protected ElementTransitionReactor core = new ElementTransitionReactor();
	protected int runTick = 0;
	protected double instableRatio = 1;
	protected double instableFragment = 0;
	protected int powerLevelLine = Integer.MAX_VALUE;

	/** 记录上次的状态，用于数据同步 */
	public Element lastReactorElement;

	public static enum ReactorInstableSection {
		NO_IELEMENT_INVENTORY(0.995),
		INVENTORY_NO_CHANGE(0.99);

		public final double chaos;

		ReactorInstableSection(double chaos) {
			this.chaos = MathHelper.clamp(chaos, Double.MIN_VALUE, 1);
		}

		public double doChaos(double factor) {
			return factor >= 1 ? chaos : Math.pow(chaos, factor);
		}
	}

	public static enum ReactorStatus {
		OFF(false),
		STANDBY(false),
		RUNNING(true),
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
	 * 启动反应堆
	 * 
	 * @return 启动状态
	 */
	public boolean launch() {
		if (world.isRemote) return false;
		if (status.isRunning) return true;
		if (status != ReactorStatus.STANDBY) return false;
		status = ReactorStatus.RUNNING;
		runTick = 0;
		core.reset();
		updateToClient();
		markDirty();
		return true;
	}

	public TileEntity getElementTile(int index) {
		float alpha = index * 3.1415926f / 4 + 3.1415926f / 8;
		int x = (int) (MathHelper.cos(alpha) / 0.99f * 6f);
		int z = (int) (MathHelper.sin(alpha) / 0.99f * 6f);
		return world.getTileEntity(pos.add(x, 0, z));
	}

	public int getPowerLine() {
		return powerLevelLine;
	}

	/** 不稳定率∈(0,1]，越大越稳定 */
	public double getInstableRatio() {
		return instableRatio;
	}

	public void setInstableRatio(double instableRatio) {
		this.instableRatio = MathHelper.clamp(instableRatio, Double.MIN_VALUE, 1);
	}

	public double getInstableFragment() {
		return instableFragment;
	}

	public double getInstableFragmentCapacity() {
		return INSTABLE_FRAGMENT_BASE_CAPACITY / getInstableRatio();
	}

	public void instable(ReactorInstableSection reason, double factor) {
		ReactorStatus status = getStatus();
		if (status != ReactorStatus.RUNNING) return;
		setInstableRatio(getInstableRatio() * reason.doChaos(factor));
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

	@Override
	public double insertMagicFragment(double count, boolean simulate) {
		return count - 1;
	}

	@Override
	public double extractMagicFragment(double count, boolean simulate) {
		return 1;
	}

	protected void tryLinkOrUnlinkMagicIR(boolean isLink) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			TileEntity tile = world.getTileEntity(pos.offset(facing, 8));
			if (tile instanceof TileIceRockCrystalBlock) {
				((TileIceRockSendRecv) tile).setBeamHandler(facing.getOpposite(), isLink ? this : null);
			}
		}
	}

	@Override
	public void update() {
		ReactorStatus status = getStatus();
		if (!status.isRunning) return;
		if (world.isRemote) updateClientEffect();
		runTick++;
		if (runTick % 10 == 0) {
			updateByIndex(runTick / 10 - 1);
			if (world.isRemote) return;
			if (lastReactorElement != core.getElement()) {
				lastReactorElement = core.getElement();
				this.updateToClient();
			}
		}
		if (runTick % 20 == 0) tryLinkOrUnlinkMagicIR(true);
	}

	/** 当关闭 */
	public void onClose() {
		setStatus(ReactorStatus.STANDBY);
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
			instable(ReactorInstableSection.NO_IELEMENT_INVENTORY, 1);
			return;
		}
		// 不是祭坛形也不可以
		IAltarWake altarWake = null;
		if (tile instanceof IAltarWake) altarWake = (IAltarWake) tile;
		if (altarWake == null) {
			instable(ReactorInstableSection.NO_IELEMENT_INVENTORY, 1);
			return;
		}
		// 好了，根据状态决策，到这里只能有 正在关闭 和 启动状态
		ReactorStatus status = getStatus();
		if (status == ReactorStatus.CLOSING) {
			// close状态
			if (updateClosing(eInv)) onClose();
			return;
		}

		// 正式进入running状态
		// 開始决定是获取还是放入，并进行操作
		int changeFlag = updateEIFragment(eInv);
		if (changeFlag == 1) altarWake.wake(IAltarWake.SEND, pos);
		else if (changeFlag == 2) altarWake.wake(IAltarWake.OBTAIN, pos);
		else {
			instable(ReactorInstableSection.INVENTORY_NO_CHANGE, 1);
			return;
		}

		if (world.isRemote) {
			playChangeEffect(tile.getPos(), changeFlag);
			return;
		}

		Element toElement = core.getSuggestTransition();
		if (toElement != null && toElement != core.getElement()) {
			core.transitTo(toElement);
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
					this.powerLevelLine = Math.min(powerLine, this.powerLevelLine) + 1;
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
		}

		return changeFlag;
	}

	protected boolean updateClosing(IElementInventory eInv) {
		for (int i = 0; i < eInv.getSlots(); i++) {
			int flag = transferToElementInventory(eInv, i, this.powerLevelLine, 1);
			if (flag == 1 || this.powerLevelLine == 1) return true;
			if (flag == 1) this.powerLevelLine = Math.max(this.powerLevelLine - 1, 1);
		}
		return false;
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
