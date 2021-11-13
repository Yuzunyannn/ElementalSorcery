package yuzunyannn.elementalsorcery.tile.md;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.tile.IAcceptMagic;
import yuzunyannn.elementalsorcery.api.tile.IAcceptMagicPesky;
import yuzunyannn.elementalsorcery.api.tile.IProvideMagic;
import yuzunyannn.elementalsorcery.block.BlockMagicTorch;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.FirewrokShap;
import yuzunyannn.elementalsorcery.util.IField;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public abstract class TileMDBase extends TileEntity implements IAcceptMagicPesky, IProvideMagic, IField {

	@Config(kind = "tile", sync = true)
	static private int MD_BASE_MAX_CAPACITY = 1000;

	@Config(kind = "tile")
	static private int MD_BASE_OVERFLOW = 800;

	@Config(kind = "tile")
	static private int MD_BASE_SEND_PRE_SECOND = 100;

	/** 给予魔力的目标 */
	protected TargetInfo[] targets = new TargetInfo[6];
	/** 魔力仓库使用 */
	protected ElementStack magic = new ElementStack(ESInit.ELEMENTS.MAGIC, 0, 25);
	/** 需求的仓库 */
	protected ItemStackHandler inventory = this.initItemStackHandler();

	/** 初始化仓库，不需要返回null */
	protected ItemStackHandler initItemStackHandler() {
		return null;
	};

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		for (int i = 0; i < targets.length; i++) {
			String key = "dir" + i;
			if (nbt.hasKey(key)) {
				int dirIndex = nbt.getByte(key);
				targets[dirIndex] = new TargetInfo(NBTHelper.getBlockPos(nbt, key));
			}
		}
		if (nbt.hasKey("magic")) magic = new ElementStack(nbt.getCompoundTag("magic"));
		if (nbt.hasKey("Inv") && this.inventory != null) {
			int slot = this.inventory.getSlots();
			this.inventory.deserializeNBT(nbt.getCompoundTag("Inv"));
			// 扩容未保存上的情况
			if (slot > this.inventory.getSlots()) {
				ElementalSorcery.logger.warn(this.getClass() + "的inventory出现扩容现象！");
				this.inventory = this.initItemStackHandler();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		// 这步记录仅仅是为了没有ITickable的tile记录的，但是貌似所有MD都tickable，貌似没有太大必要
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == null) continue;
			String key = "dir" + i;
			nbt.setByte(key, (byte) i);
			NBTHelper.setBlockPos(nbt, key, targets[i].pos);
		}
		if (!magic.isEmpty()) nbt.setTag("magic", magic.serializeNBT());
		if (inventory != null) nbt.setTag("Inv", this.inventory.serializeNBT());
		return super.writeToNBT(nbt);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) return (T) inventory;
		return super.getCapability(capability, facing);
	}

	/** 记录目标的数据 */
	public class TargetInfo implements IAcceptMagic {

		final public BlockPos pos;
		public IAcceptMagic accepter;
		public int lev;
		/** 动态等级变化 */
		public int dyLev;

		public TargetInfo(BlockPos pos) {
			this.pos = pos;
			try {
				// 这里检测，很有可能找不到world等异常
				this.check();
			} catch (Exception e) {}
		}

		public TargetInfo(BlockPos pos, IAcceptMagic accepter) {
			this.pos = pos;
			this.accepter = accepter;
			this.init();
		}

		public void init() {
			if (this.accepter instanceof IAcceptMagicPesky) {
				this.lev = ((IAcceptMagicPesky) this.accepter).requireLevel() + 1;
			}
			if (this.lev <= 0) this.lev = 1;
		}

		public int getLevel() {
			return lev + dyLev;
		}

		public <T> T to(Class<T> cls) {
			return accepter != null && cls.isAssignableFrom(accepter.getClass()) ? (T) accepter : null;
		}

		public boolean check() {
			if (this.accepter == null) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile instanceof IAcceptMagic) {
					this.accepter = (IAcceptMagic) tile;
					this.init();
					return true;
				} else return false;
			}
			TileEntity tile = world.getTileEntity(pos);
			if (tile == this.accepter) return true;
			if (tile instanceof IAcceptMagic) {
				this.accepter = (IAcceptMagic) tile;
				this.init();
				return true;
			}
			return false;
		}

		@Override
		public ElementStack accpetMagic(ElementStack magic, BlockPos from, EnumFacing facing) {
			if (this.accepter instanceof IAcceptMagicPesky) {
				IAcceptMagicPesky accepter = (IAcceptMagicPesky) this.accepter;
				if (!accepter.canRecvMagic(facing)) return magic;
				if (magic.getPower() < accepter.requireMinMagicPower()) return magic;
				if (magic.getCount() < accepter.requireMinMagicCount()) return magic;
				int rest = accepter.getMaxCapacity() - accepter.getCurrentCapacity();
				if (rest < 0) return magic;
				rest = Math.min(magic.getCount(), rest);
				ElementStack remain = accepter.accpetMagic(magic.splitStack(rest), from, facing);
				magic.grow(remain);
				return magic;
			} else return this.accepter.accpetMagic(magic, from, facing);
		}
	}

	/** 寻找一个目标 */
	public TargetInfo findTarget(BlockPos pos, EnumFacing facing, int distance) {
		for (int i = 0; i < distance; i++) {
			pos = pos.offset(facing);
			IBlockState state = this.world.getBlockState(pos);
			if (state.isOpaqueCube()) return null;
			TileEntity tile = this.world.getTileEntity(pos);
			if (tile == null) continue;
			if (tile instanceof IAcceptMagic) return new TargetInfo(pos, (IAcceptMagic) tile);
			return null;
		}
		return null;
	}

	/** 当这个tile被放下，开始寻找四周的accpter */
	public void coming() {
		for (EnumFacing facing : EnumFacing.VALUES) {
			if (this.canSend(facing)) this.find(facing, true);
			else {
				// 没有火把的情况下，由于是第一出来，也会强行说hi
				TargetInfo info = this.findTarget(this.pos, facing, this.getDistance());
				if (info != null) {
					TileMDBase tile = info.to(TileMDBase.class);
					if (tile != null) tile.hi(this.pos, facing.getOpposite());
				}
			}
		}
	}

	/** 当这个tile离开 ,应该在自身已经移除世界后调用 */
	public void leaving(BlockPos pos) {
		for (EnumFacing facing : EnumFacing.VALUES) {
			TargetInfo info = this.findTarget(pos, facing, this.getDistance());
			if (info != null) {
				TileMDBase tile = info.to(TileMDBase.class);
				if (tile != null) tile.bye(pos, facing.getOpposite());
			}
		}
	}

	static final public int[] PARTICLE_COLOR = new int[] { 0x7d17e3 };
	static final public int[] PARTICLE_COLOR_FADE = new int[] { 0x9322b5 };

	/** 当被被破坏 */
	public void onBreak() {
		if (!this.world.isRemote) {
			if (this.inventory != null) BlockHelper.drop(inventory, world, pos);
			if (this.getCurrentCapacity() == 0) return;
			ElementExplosion.doExplosion(world, pos, magic, null);
		}
	}

	/** 当指定方向的方块改变了 */
	public void change(EnumFacing facing) {
		this.find(facing, true);
	}

	/** 寻找并连接到 */
	public void find(EnumFacing facing, boolean sayHi) {
		int index = facing.getIndex();
		// 不能发送的时候
		if (!this.canSend(facing)) {
			targets[index] = null;
			this.torch(facing, false);
			this.markDirty();
			return;
		}
		// 寻找目标
		targets[index] = this.findTarget(this.pos, facing, this.getDistance());
		if (targets[index] != null) {
			// 如果对面不能接受
			IAcceptMagicPesky magicPesky = targets[index].to(IAcceptMagicPesky.class);
			if (magicPesky != null && !magicPesky.canRecvMagic(facing.getOpposite())) targets[index] = null;
			// 如果对面是发送者
			if (sayHi) {
				IProvideMagic magicProvide = targets[index].to(IProvideMagic.class);
				magicProvide.hi(this.pos, facing.getOpposite());
			}
		}
		if (targets[index] == null) this.torch(facing, false);
		this.markDirty();

	}

	/** 其他tile找到自己的时候 */
	@Override
	public void hi(BlockPos from, EnumFacing facing) {
		this.find(facing, false);
		int index = facing.getIndex();
		if (targets[index] != null && targets[index].pos.equals(from)) {
			TileMDBase tile = targets[index].to(TileMDBase.class);
			if (tile != null) tile.oh(this.pos, facing.getOpposite());
		}
	}

	/** 打招呼后，对面tile也选定了自己 */
	public void oh(BlockPos from, EnumFacing facing) {

	}

	/** 其他方块不再离开时候，向四周的方跨说再见 */
	public void bye(BlockPos from, EnumFacing facing) {
		this.find(facing, true);
		int index = facing.getIndex();
		if (targets[index] == null) this.torch(facing, false);
	}

	/** 检查所有位置状态 */
	public void checkAll() {
		for (int i = 0; i < targets.length; i++) this.find(EnumFacing.getFront(i), true);
	}

	/** 关闭所有火把 */
	protected void allTorchOff() {
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == null) continue;
			this.torch(EnumFacing.getFront(i), false);
		}
	}

	/** 调整动态的优先级 */
	protected void fixedDyLev() {

	}

	/** 进行一次传输 */
	protected void transferOnce() {
		if (this.getCurrentCapacity() < this.getOverflow()) {
			this.allTorchOff();
			return;
		}
		// 调整动态lev
		this.fixedDyLev();
		// 总共能量
		int sumLevel = 0;
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == null) continue;
			sumLevel += targets[i].getLevel();
		}
		if (sumLevel == 0) { return; }
		// 发送量
		int sendCount = this.getMaxSendOnce();
		sendCount = Math.min(sendCount, this.magic.getCount());
		ElementStack sendMagic = this.magic.splitStack(sendCount);
		if (sendCount <= 0) {
			this.allTorchOff();
			return;
		}
		// 发送
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == null) continue;
			EnumFacing facing = EnumFacing.getFront(i);
			if (sendMagic.isEmpty()) {
				this.torch(facing, false);
				continue;
			}
			int count = targets[i].getLevel() * sendCount / sumLevel;
			count = count == 0 ? 1 : count;
			ElementStack send = sendMagic.splitStack(count);
			ElementStack remain = targets[i].accpetMagic(send, this.pos, facing.getOpposite());
			if (remain.isEmpty() || remain.getCount() != count) this.torch(facing, true);
			else this.torch(facing, false);
			sendMagic.grow(remain);
		}
		this.magic.grow(sendMagic);
		this.markDirty();
	}

	@Override
	public ElementStack accpetMagic(ElementStack magic, BlockPos from, EnumFacing facing) {
		int rest = this.getMaxCapacity() - this.getCurrentCapacity();
		if (rest < 0) return magic;
		rest = Math.min(magic.getCount(), rest);
		if (this.magic.isEmpty()) this.magic = magic.splitStack(rest);
		else this.magic.grow(magic.splitStack(rest));
		this.markDirty();
		return magic;
	}

	/** 进行一次传输 */
	@SideOnly(Side.CLIENT)
	protected void transferClientEffect() {
		if (tick % 2 != 0) return;
		for (int i = 0; i < targets.length; i++) {
			EnumFacing facing = EnumFacing.getFront(i);
			if (this.hasTorch(facing) && this.canSend(facing)) {
				if (this.world.getBlockState(pos.offset(facing)).getValue(BlockMagicTorch.LIT)) {
					// 客户端和服务器并没有同步这些数据，想要展示效果需要重新找，当然是用autoTransfer会自动查找，这部分可能是多余的
					if (targets[i] == null) this.find(facing, false);
					else if (!targets[i].check()) this.find(facing, false);
					// 这种情况应该不存在
					if (targets[i] == null) continue;
					this.effectTo(facing, targets[i].pos);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void effectTo(EnumFacing facing, BlockPos to) {
		BlockPos pos = this.pos.offset(facing);
		if (pos.equals(to)) return;
		FirewrokShap.createSparkUniformlySpeed(world, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5,
				to.getX() + 0.5, to.getY() + 0.5, to.getZ() + 0.5, 0.2f, TileMDBase.PARTICLE_COLOR,
				TileMDBase.PARTICLE_COLOR_FADE, false, false);
	}

	/** 记录的tick */
	protected int tick;

	/** 获取每次发送的量 */
	protected int getMaxSendOnce() {
		return this.getMaxSendPreSecond() / 4;
	}

	/** 自动发送 */
	protected void autoTransfer() {
		tick++;
		if (tick % 5 != 0) return;
		// 2秒进行全部遍历一次
		if (tick % 40 == 0) {
			this.checkAll();
		}
		if (this.world.isRemote) {
			this.transferClientEffect();
		} else {
			this.transferOnce();
		}
	}

	/** 判断制定方向是否有torch */
	public boolean hasTorch(EnumFacing facing) {
		IBlockState state = this.world.getBlockState(pos.offset(facing));
		return state.getBlock() == ESInit.BLOCKS.MAGIC_TORCH && state.getValue(BlockMagicTorch.FACING) == facing;
	}

	/** 设置火把开关 */
	public void torch(EnumFacing facing, boolean open) {
		if (!this.hasTorch(facing)) return;
		BlockPos pos = this.pos.offset(facing);
		IBlockState state = this.world.getBlockState(pos);
		if (state.getValue(BlockMagicTorch.LIT) == open) return;
		this.world.setBlockState(pos, state.withProperty(BlockMagicTorch.LIT, open));
	}

	/** 判断是否可以接受 */
	@Override
	public boolean canRecvMagic(EnumFacing facing) {
		return facing != EnumFacing.UP;
	}

	/** 判断方向是否可以发送 */
	protected boolean canSend(EnumFacing facing) {
		return facing.getHorizontalIndex() >= 0 && this.hasTorch(facing);
	}

	/** 获取距离 */
	protected int getDistance() {
		return 16;
	}

	/** 获取每秒的最大数量，需要在update中使用 autoTransfer函数 */
	protected int getMaxSendPreSecond() {
		return MD_BASE_SEND_PRE_SECOND;
	}

	/** 获取达到多少值就可以向外发送了，超过了就会发送 */
	protected int getOverflow() {
		return MD_BASE_OVERFLOW;
	}

	@Override
	public int getMaxCapacity() {
		return MD_BASE_MAX_CAPACITY;
	}

	@Override
	public int getCurrentCapacity() {
		return magic.getCount();
	}

	public int getCurrentPower() {
		return magic.isEmpty() ? 0 : magic.getPower();
	}

	protected void setCurrentCapacity(int count) {
		this.magic.setCount(count);
	}

	protected void magicShrink(int size) {
		this.magic.shrink(size);
	}

	@Override
	public int requireMinMagicPower() {
		return 0;
	}

	@Override
	public int requireMinMagicCount() {
		return 0;
	}

	@Override
	public int requireLevel() {
		return 0;
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
			return this.magic.getCount();
		case 1:
			return this.magic.getPower();
		default:
			return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
			this.magic.setCount(value);
			return;
		case 1:
			this.magic.setPower(value);
			return;
		}
	}

	@Override
	public int getFieldCount() {
		return 2;
	}

	/** 设置nbt数据 */
	protected NBTTagCompound updateDataWriteToNBT(NBTTagCompound nbt, int fieldIndex) {
		if (nbt.hasKey("field", 10)) nbt = nbt.getCompoundTag("field");
		else nbt.setTag("field", nbt = new NBTTagCompound());
		nbt.setInteger(Integer.toString(fieldIndex), this.getField(fieldIndex));
		return nbt;
	}

	/** 将仓库变更写入nbt */
	protected NBTTagCompound writeInventoryChangeToNBT(NBTTagCompound nbt, int slot, ItemStack stack) {
		NBTTagList list;
		if (nbt.hasKey("inv", 9)) list = nbt.getTagList("inv", 10);
		else nbt.setTag("inv", list = new NBTTagList());
		NBTTagCompound temp = new NBTTagCompound();
		temp.setInteger("slot", slot);
		temp.setTag("item", stack.serializeNBT());
		list.appendTag(temp);
		return nbt;
	}

	/** 将数据更新到client端 */
	public void updateToClient(NBTTagCompound nbt) {
		if (world.isRemote) return;
		SPacketUpdateTileEntity packet = this.getSendUpdatePacket(nbt);
		for (EntityPlayer player : world.playerEntities) {
			if (player.getPosition().distanceSq(this.pos) > 256 * 256) continue;
			((EntityPlayerMP) player).connection.sendPacket(packet);
		}
	}

	/** 获取发送包 */
	public SPacketUpdateTileEntity getSendUpdatePacket(NBTTagCompound nbt) {
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), nbt);
	}

	/** 客户端受到nbt最后会调用，处理自定义信息 */
	public void customUpdate(NBTTagCompound nbt) {

	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		if (tag.hasKey("field", 10)) {
			NBTTagCompound nbt = tag.getCompoundTag("field");
			for (int i = 0; i < this.getFieldCount(); i++) {
				String key = Integer.toString(i);
				if (nbt.hasKey(key)) this.setField(i, nbt.getInteger(key));
			}
		}
		if (tag.hasKey("inv")) {
			NBTTagList list = tag.getTagList("inv", 10);
			for (NBTBase base : list) {
				NBTTagCompound nbg = (NBTTagCompound) base;
				int slot = nbg.getInteger("slot");
				if (slot < 0 || slot >= inventory.getSlots()) continue;
				ItemStack stack = new ItemStack(nbg.getCompoundTag("item"));
				if (stack.isEmpty()) continue;
				inventory.setStackInSlot(slot, stack);
			}
		}
		this.customUpdate(tag);
	}
}
