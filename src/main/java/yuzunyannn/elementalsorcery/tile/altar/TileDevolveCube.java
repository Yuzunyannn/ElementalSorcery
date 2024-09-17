package yuzunyannn.elementalsorcery.tile.altar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IElementInventoryPromote;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.ContainerMapDetecter;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementAbsorb;
import yuzunyannn.elementalsorcery.tile.TileEntityNetworkOld;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryMonitor;
import yuzunyannn.elementalsorcery.util.element.ElementStackDoubleExchanger;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class TileDevolveCube extends TileEntityNetworkOld implements ITickable {

	static public class DevolveData {

		public boolean inEnable;
		public boolean outEnable;
		public short count;

		public DevolveData() {

		}

		public DevolveData(int flag) {
			fromFlag(flag);
		}

		public int toFlag() {
			int flag = 0;
			if (inEnable) flag = flag | 0x1;
			if (outEnable) flag = flag | 0x2;
			flag = flag | (count << 16);
			return flag;
		}

		public void fromFlag(int flag) {
			if ((flag & 0x1) != 0) inEnable = true;
			if ((flag & 0x2) != 0) outEnable = true;
			count = (short) ((flag >> 16) & 0xffff);
		}

		public DevolveData copy() {
			DevolveData dat = new DevolveData();
			dat.count = this.count;
			dat.inEnable = this.inEnable;
			dat.outEnable = this.outEnable;
			return dat;
		}

	}

	public long colorVersion; // 地图
	public long selectedVersion; // 元素容器
	public long exchangerVersion; // 自身元素

	/** 检查范围，固定半径16 */
	static public final int DETECTION_RANGE = 16;
	/** 自身容器大小，固定为4 */
	static public final int SLOT_COUNT = 4;

	@Config
	static public short MAX_AUTO_TRANSFER_COUNT = 8;
	@Config(sync = true)
	static public int MAX_AUTO_TRANSFER_VOLUME_PER_SEC = 512;

	protected int tick;

	/** 所有地图像素点的颜色code */
	protected byte[] colors = new byte[(DETECTION_RANGE * 2) * (DETECTION_RANGE * 2)];
	/** 所有的元素容器map */
	protected Map<BlockPos, DevolveData> elementContainer = new HashMap<>();
	/** 地图当前遍历的index位置 */
	protected int ergodicIndex = 0;
	/** 自身的元素容器 */
	protected ElementStackDoubleExchanger exchanger = ElementInventory.sensor(new ElementStackDoubleExchanger(
			SLOT_COUNT), ElementInventoryMonitor.sensor(this));
	/** 自动传输列表，为elementContainer的value in/outEnable的记录set */
	protected Set<BlockPos> autoTransferSet = new HashSet<>();
	/** 自动传输列表的更新mark用于数据同步 */
	protected boolean autoTransferSetChangeMark;
	/** 用于container数据检查更新elementContainer的句柄 */
	protected ContainerMapDetecter.ICanMapDetected<BlockPos, DevolveData, NBTTagIntArray, NBTTagInt> canMapDetected = new ContainerMapDetecter.ICanMapDetected<BlockPos, DevolveData, NBTTagIntArray, NBTTagInt>() {

		@Override
		public Collection getKeys() {
			return elementContainer.keySet();
		}

		@Override
		public boolean hasChange(BlockPos key, DevolveData oldValue) {
			DevolveData dat = elementContainer.get(key);
			if (dat == null) return oldValue != null;
			if (oldValue == null) return dat != null;
			if (dat.count != oldValue.count) return true;
			if (dat.inEnable != oldValue.inEnable) return true;
			if (dat.outEnable != oldValue.outEnable) return true;
			return false;
		}

		@Override
		public DevolveData copyCurrValue(BlockPos key) {
			return elementContainer.get(key).copy();
		}

		@Override
		public NBTTagInt serializeCurrValueToSend(BlockPos key) {
			return new NBTTagInt(elementContainer.get(key).toFlag());
		}

		@Override
		public NBTTagIntArray serializeCurrKeyToSend(BlockPos key) {
			return new NBTTagIntArray(NBTHelper.toIntArray(key));
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void deserializeCurrKVFromSend(NBTTagIntArray key, NBTTagInt nbtData) {
			BlockPos pos = NBTHelper.toBlockPos(key.getIntArray());
			if (nbtData == null) elementContainer.remove(pos);
			else elementContainer.put(pos, new DevolveData(nbtData.getInt()));
		}

	};

	public byte[] getColors() {
		return colors;
	}

	public Map<BlockPos, DevolveData> getElementContainers() {
		return elementContainer;
	}

	public ElementStackDoubleExchanger getExchanger() {
		return exchanger;
	}

	public ContainerMapDetecter.ICanMapDetected<BlockPos, DevolveData, NBTTagIntArray, NBTTagInt> getCanMapDetected() {
		return canMapDetected;
	}

	@SideOnly(Side.CLIENT)
	public void setColors(byte[] colors) {
		this.colors = colors;
	}

	@SideOnly(Side.CLIENT)
	public int getColorByColorByteValue(int byteValue) {
		if (byteValue == 0) return 0x19111d;
		int colorIndex = byteValue / 4;
		int colorNum = byteValue % 3;
		if (colorIndex < 0 || colorIndex >= MapColor.COLORS.length) return 0;
		return MapColor.COLORS[colorIndex].getMapColor(colorNum);
	}

	protected int getColorByteIndex(int x, int z) {
		return (x + DETECTION_RANGE) + (z + DETECTION_RANGE) * (DETECTION_RANGE * 2);
	}

	public void markColorDirty() {
		this.markDirty();
		colorVersion = System.currentTimeMillis();
	}

	public void markSelectedDirty() {
		this.markDirty();
		selectedVersion = System.currentTimeMillis();
	}

	public void markExchangerDirty() {
		this.markDirty();
		exchangerVersion = System.currentTimeMillis();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (this.isSending()) {
			int[] datas = new int[autoTransferSet.size() * 4];
			int i = 0;
			for (BlockPos pos : autoTransferSet) {
				datas[i] = pos.getX();
				datas[++i] = pos.getY();
				datas[++i] = pos.getZ();
				datas[++i] = elementContainer.get(pos).toFlag();
				i++;
			}
			compound.setIntArray("atay", datas);
			return super.writeToNBT(compound);
		}
		NBTHelper.setBlockPosCollection(compound, "atsfs", autoTransferSet);
		compound.setByteArray("cbts", colors);
		NBTTagList list = new NBTTagList();
		compound.setTag("ddm", list);
		for (Entry<BlockPos, DevolveData> entry : elementContainer.entrySet()) {
			NBTTagCompound dat = new NBTTagCompound();
			dat.setInteger("F", entry.getValue().toFlag());
			NBTHelper.setBlockPos(dat, "lP", entry.getKey());
			list.appendTag(dat);
		}
		exchanger.saveState(compound);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (this.isSending()) {
			int[] datas = compound.getIntArray("atay");
			int length = (datas.length / 4) * 4;
			autoTransferSet = new HashSet<>();
			for (int i = 0; i < length; i += 4) {
				BlockPos pos = new BlockPos(datas[i], datas[i + 1], datas[i + 2]);
				autoTransferSet.add(pos);
				elementContainer.put(pos, new DevolveData(datas[i + 3]));
			}
			return;
		}
		autoTransferSet = NBTHelper.getBlockPosSet(compound, "atsfs");
		colors = compound.getByteArray("cbts");
		if (colors.length < DETECTION_RANGE * 2) colors = new byte[(DETECTION_RANGE * 2) * (DETECTION_RANGE * 2)];
		NBTTagList list = compound.getTagList("ddm", NBTTag.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound dat = list.getCompoundTagAt(i);
			BlockPos pos = NBTHelper.getBlockPos(dat, "lP");
			elementContainer.put(pos, new DevolveData(dat.getInteger("F")));
		}
		exchanger.loadState(compound);

		colorVersion = 1;
		selectedVersion = 1;
		exchangerVersion = 1;
	}

	/** 获取可以自动传输节点的最大个数 */
	public int getMaxAutoTransferCount() {
		return MAX_AUTO_TRANSFER_COUNT;
	}

	/** 获取自动传输时没秒的最大传送量 */
	public int getAutoMaxCountPerSec() {
		return MAX_AUTO_TRANSFER_VOLUME_PER_SEC;
	}

	public boolean tryUpdateDetectionMark;
	public boolean hasOneTrun = false;
	public float renderTick = 0;
	public float prevRenderTick = 0;
	public float renderTickGrow = 0.25f;

	@Override
	public void update() {
		tick++;
		prevRenderTick = renderTick;
		renderTick += renderTickGrow;
		if (tick % 20 == 0) {
			if (renderTickGrow > 0.25f) renderTickGrow = Math.max(0.25f, renderTickGrow - 0.1f);
			updateAutoTranfer();
		}
		if (world.isRemote) return;
		// 扫描地图，有标记，加快进行，没标记，5分钟龟速扫描，减少资源占用
		if (tryUpdateDetectionMark) {
			tryUpdateDetectionMark = false;
			if (tick % 10 == 0) updateWithTimes(16 + RandomHelper.rand.nextInt(64));
		} else {
			if (hasOneTrun) {
				if (tick % (20 * 60 * 5) == 0) updateWithTimes(16 + RandomHelper.rand.nextInt(64));
			} else {
				if (tick % 200 == 0) updateWithTimes(16 + RandomHelper.rand.nextInt(64));
			}
		}
		if (autoTransferSetChangeMark) {
			this.updateToClient();
			autoTransferSetChangeMark = false;
		}
	}

	public static BlockPos ergodicFunction(int n) {
		int i = n % 4;
		n = n / 4;
		if (i == 0) return new BlockPos(n % DETECTION_RANGE, 0, n / DETECTION_RANGE);
		else if (i == 1) return new BlockPos(-n / DETECTION_RANGE - 1, 0, n % DETECTION_RANGE);
		else if (i == 2) return new BlockPos(-n % DETECTION_RANGE - 1, 0, -n / DETECTION_RANGE - 1);
		else return new BlockPos(n / DETECTION_RANGE, 0, -n % DETECTION_RANGE - 1);
	}

	// 尝试更新多少次
	protected void updateWithTimes(int times) {
		for (int n = 0; n < times; n++) {
			BlockPos p = ergodicFunction(ergodicIndex++);
			int i = getColorByteIndex(p.getX(), p.getZ());
			if (i >= 0 && i < colors.length) refreshDetectionAt(p.getX(), p.getZ());
			else {
				ergodicIndex = 0;
				hasOneTrun = true;
			}
		}
	}

	// 刷新x 和 z 的数据
	protected boolean refreshDetectionAt(int x, int z) {
		int i = getColorByteIndex(x, z);
		if (i < 0 || i >= colors.length) return false;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(this.pos.getX() + x,
				Math.min(this.pos.getY() + DETECTION_RANGE, 230), this.pos.getZ() + z);

		Chunk chunk = world.getChunk(pos);
		if (chunk.isEmpty()) return false;

		byte colorByte = 0;
		boolean elementContainerFind = false;

		for (; pos.getY() > 0 && pos.getY() >= this.pos.getY() - DETECTION_RANGE; pos.setY(pos.getY() - 1)) {
			IBlockState state = chunk.getBlockState(pos);
			if (state == Blocks.AIR.getDefaultState()) continue;
			MapColor mapColor = state.getMapColor(world, pos);
			if (colorByte == 0 && mapColor != MapColor.AIR) {
				byte colorNum = 2;
				// if (pos.getY() <= this.pos.getY()) colorNum = 1;
				colorByte = (byte) ((mapColor.colorIndex << 2) | colorNum);
			}
			Block block = state.getBlock();
			if (block instanceof ITileEntityProvider) {
				TileEntity tileEntity = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
				IElementInventory eInv = ElementHelper.getElementInventory(tileEntity);
				boolean canTransfer = eInv != null;
				if (tileEntity instanceof IElementInventoryPromote)
					canTransfer = ((IElementInventoryPromote) tileEntity).canInventoryOperateBy(this);
				if (canTransfer) {
					BlockPos at = new BlockPos(pos);
					if (!elementContainer.containsKey(at)) {
						elementContainer.put(at, new DevolveData());
						elementContainerFind = true;
					}
				}
			}
		}

		if (elementContainerFind) this.markSelectedDirty();
		if (colors[i] == colorByte) return elementContainerFind;
		colors[i] = colorByte;
		this.markColorDirty();
		return true;
	}

	public void removeDevolveData(BlockPos pos) {
		DevolveData dat = elementContainer.remove(pos);
		if (dat == null) return;
		markSelectedDirty();
		if (dat.inEnable || dat.outEnable) {
			if (autoTransferSet != null) autoTransferSetChangeMark = autoTransferSet.remove(pos);
		}
	}

	public void setDevolveData(BlockPos pos, DevolveData dat) {
		markSelectedDirty();
		DevolveData originDat = elementContainer.get(pos);
		elementContainer.put(pos, dat);
		// 最大数目限制
		if (autoTransferSet.size() >= getMaxAutoTransferCount()) {
			if (originDat == null) {
				dat.inEnable = dat.outEnable = false;
				return;
			}
			boolean needInSet = originDat.inEnable || originDat.outEnable;
			if (!needInSet) {
				dat.inEnable = dat.outEnable = false;
				return;
			}
		}
		// 更新set
		setOut: {
			boolean needInSet = dat.inEnable || dat.outEnable;
			if (originDat == null) {
				if (needInSet) autoTransferSetChangeMark = autoTransferSet.add(pos);
				else autoTransferSetChangeMark = autoTransferSet.remove(pos);
				break setOut;
			}
			if (originDat.inEnable == dat.inEnable && originDat.outEnable == dat.outEnable) break setOut;
			if (needInSet) autoTransferSet.add(pos);
			else autoTransferSet.remove(pos);
			autoTransferSetChangeMark = true;
		}
	}

	public boolean updateDevolveData(BlockPos pos, DevolveData dat) {
		DevolveData originDat = elementContainer.get(pos);
		if (originDat == null) return false;
		if (ElementHelper.getElementInventory(world.getTileEntity(pos)) == null) {
			removeDevolveData(pos);
			return false;
		}
		setDevolveData(pos, dat);
		return true;
	}

	public Set<BlockPos> getAutoTransferSet() {
		return autoTransferSet;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		if (tag.hasKey("E")) {
			playTranferEffectClient(NBTHelper.getBlockPos(tag, "P"), tag.getInteger("E"), tag.getInteger("C"));
			return;
		}
		super.handleUpdateTag(tag);
	}

	public void playTranferEffectServer(BlockPos pos, int sendType, int color) {
		if (world.isRemote) return;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("E", (byte) sendType);
		nbt.setInteger("C", color);
		NBTHelper.setBlockPos(nbt, "P", pos);
		updateToClient(nbt);
	}

	@SideOnly(Side.CLIENT)
	public void playTranferEffectClient(BlockPos pos, int sendType, int color) {

		renderTickGrow = 1.5f;

		DevolveData devolveDat = elementContainer.get(pos);
		int tryTimes = devolveDat == null ? 1 : MathHelper.clamp(devolveDat.count / 64, 1, 6);

		Vec3d vec1 = new Vec3d(pos).add(0.5, 0.5, 0.5);
		Vec3d vec2 = new Vec3d(this.pos).add(0.5, 0.5, 0.5);

		for (int i = 0; i < tryTimes; i++) {
			EffectElementAbsorb effect;
			if (sendType == IAltarWake.OBTAIN) effect = new EffectElementAbsorb(world, vec2, vec1);
			else effect = new EffectElementAbsorb(world, vec1, vec2);

			effect.setColor(color);
			effect.startTick = Effect.rand.nextInt(10) + 5;
			effect.randMotion(effect.startTick / 80.0f);

			Effect.addEffect(effect);
		}

		IAltarWake alterWake = BlockHelper.getTileEntity(world, pos, IAltarWake.class);
		if (alterWake != null) alterWake.wake(sendType, this.pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox();
	}

	/**
	 * 进行传输
	 * 
	 * @param flag 0x1客户端自行播放effect <br>
	 *             0x2服务端将effect同步到客户端<br>
	 *             0x4服务端强制刷新statusChange<br>
	 *             0x8来自自动的传送<br>
	 * @return 传输的stack
	 */
	public ElementStack doTranfer(BlockPos pos, int sendType, int countLimt, int flag) {

		DevolveData devolveDat = elementContainer.get(pos);
		if (devolveDat == null) return ElementStack.EMPTY;
		int count = Math.min(countLimt, devolveDat.count);
		if (count <= 0) return ElementStack.EMPTY;

		TileEntity tileEntity = world.getTileEntity(pos);
		IElementInventory eInv = ElementHelper.getElementInventory(tileEntity);

		if (eInv == null) {
			removeDevolveData(pos);
			return ElementStack.EMPTY;
		}

		ElementStack tranferStack = ElementStack.EMPTY;
		IAltarWake altarWake = null;
//		boolean hasElement = false;
//		IElementInventoryPromote eInvPromote = null;
//		boolean changeStatus = (flag & 0x4) != 0;
		// boolean isAuto = (flag & 0x8) != 0;

		if (tileEntity instanceof IElementInventoryPromote) {
			IElementInventoryPromote eInvPromote = (IElementInventoryPromote) tileEntity;
			if (!eInvPromote.canInventoryOperateBy(this)) {
				removeDevolveData(pos);
				return ElementStack.EMPTY;
			}
		}
		if (tileEntity instanceof IAltarWake) altarWake = (IAltarWake) tileEntity;

		if (sendType == IAltarWake.SEND) {
			if (!ElementHelper.canExtract(eInv)) return ElementStack.EMPTY;
			for (int i = 0; i < eInv.getSlots(); i++) {
				ElementStack eStack = eInv.getStackInSlot(i);
				if (eStack.isEmpty()) continue;
//				hasElement = true;
				eStack = eStack.copy();
				eStack.setCount(Math.min(count, eStack.getCount()));
				tranferStack = eInv.extractElement(eStack, true);
				if (tranferStack.isEmpty()) continue;
				if (exchanger.insertElement(tranferStack, false)) {
					eInv.extractElement(eStack, false);
					break;
				}
			}
//			if (hasElement && ElementHelper.isEmpty(eInv)) changeStatus = true;
		} else {
			if (!ElementHelper.canInsert(eInv)) return ElementStack.EMPTY;
//			hasElement = !ElementHelper.isEmpty(eInv);
			for (int i = 0; i < exchanger.getSlots(); i++) {
				ElementStack eStack = exchanger.getStackInSlot(i);
				if (eStack.isEmpty()) continue;
				ElementStack teStack = eStack.copy();
				teStack.setCount(Math.min(count, teStack.getCount()));
				if (!eInv.insertElement(teStack, false)) continue;
				eStack.shrink(teStack.getCount());
				tranferStack = teStack;
				break;
			}
//			if (!hasElement && !tranferStack.isEmpty()) changeStatus = true;
		}

		if (tranferStack.isEmpty()) return ElementStack.EMPTY;
		int color = tranferStack.getColor();
		this.markExchangerDirty();

		if (world.isRemote) {
			if ((flag & 0x1) != 0) playTranferEffectClient(pos, sendType, color);
			return tranferStack;
		}
		if ((flag & 0x2) != 0) playTranferEffectServer(pos, sendType, color);

//		if (eInvPromote != null && changeStatus) eInvPromote.onInventoryStatusChange();
		if (altarWake != null) altarWake.wake(sendType, pos);
		if ((flag & 0x4) != 0) {
			tileEntity.markDirty();
			BlockHelper.sendTileUpdate(tileEntity);
		} else eInv.markDirty();

		return tranferStack;
	}

	/** 更新自动传输 */
	public void updateAutoTranfer() {
		Set<BlockPos> transferSet = getAutoTransferSet();

		List<BlockPos> inPos = new ArrayList<>();
		List<BlockPos> outPos = new ArrayList<>();

		for (BlockPos pos : transferSet) {
			DevolveData dat = elementContainer.get(pos);
			if (dat.inEnable) inPos.add(pos);
			if (dat.outEnable) outPos.add(pos);
		}

		Collections.sort(inPos, (p1, p2) -> {
			DevolveData d1 = elementContainer.get(p1);
			DevolveData d2 = elementContainer.get(p2);
			return d2.count - d1.count;
		});
		Collections.sort(outPos, (p1, p2) -> {
			DevolveData d1 = elementContainer.get(p1);
			DevolveData d2 = elementContainer.get(p2);
			return d2.count - d1.count;
		});

		int maxCount = getAutoMaxCountPerSec();

		for (BlockPos pos : outPos) {
			ElementStack estack = doTranfer(pos, IAltarWake.OBTAIN, maxCount, 0x1 | 0x8);
			maxCount = maxCount - estack.getCount();
			if (maxCount <= 0) return;
		}

		for (BlockPos pos : inPos) {
			ElementStack estack = doTranfer(pos, IAltarWake.SEND, maxCount, 0x1 | 0x8);
			maxCount = maxCount - estack.getCount();
			if (maxCount <= 0) return;
		}

	}
}
