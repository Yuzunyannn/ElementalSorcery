package yuzunyannn.elementalsorcery.tile.md;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ability.IAcceptMagic;
import yuzunyannn.elementalsorcery.api.ability.IAcceptMagicPesky;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.block.BlockMagicTorch;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public abstract class TileMDBase extends TileEntity implements IAcceptMagicPesky {

	/** 给予魔力的目标 */
	protected TargetInfo[] targets = new TargetInfo[6];
	/** 魔力仓库使用 */
	protected ElementStack magic = ElementStack.EMPTY;

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
		if (nbt.hasKey("magic"))
			magic = new ElementStack(nbt.getCompoundTag("magic"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == null)
				continue;
			String key = "dir" + i;
			nbt.setByte(key, (byte) i);
			NBTHelper.setBlockPos(nbt, key, targets[i].pos);
		}
		if (!magic.isEmpty())
			nbt.setTag("magic", magic.serializeNBT());
		return super.writeToNBT(nbt);
	}

	/** 记录目标的数据 */
	public class TargetInfo implements IAcceptMagic {

		final public BlockPos pos;
		public IAcceptMagic accepter;
		public int lev;

		public TargetInfo(BlockPos pos) {
			this.pos = pos;
			try {
				// 这里检测，很有可能找不到world等异常
				this.check();
			} catch (Exception e) {
			}
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
			if (this.lev <= 0)
				this.lev = 1;
		}

		public <T> T to(Class<T> cls) {
			if (cls.isAssignableFrom(accepter.getClass()))
				return (T) accepter;
			return null;
		}

		public boolean check() {
			if (this.accepter == null) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile instanceof IAcceptMagic) {
					this.accepter = (IAcceptMagic) tile;
					this.init();
					return true;
				} else
					return false;
			}
			TileEntity tile = world.getTileEntity(pos);
			if (tile == this.accepter)
				return true;
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
				if (magic.getPower() < accepter.requireMinMagicPower())
					return magic;
				if (magic.getCount() < accepter.requireMinMagicCount())
					return magic;
				int rest = accepter.getMaxCapacity() - accepter.getCurrentCapacity();
				rest = Math.min(magic.getCount(), rest);
				ElementStack remain = accepter.accpetMagic(magic.splitStack(rest), from, facing);
				magic.grow(remain);
				return magic;
			} else
				return this.accepter.accpetMagic(magic, from, facing);
		}
	}

	/** 寻找一个目标 */
	public TargetInfo findTarget(BlockPos pos, EnumFacing facing, int distance) {
		for (int i = 0; i < distance; i++) {
			pos = pos.offset(facing);
			IBlockState state = this.world.getBlockState(pos);
			if (state.isOpaqueCube())
				return null;
			TileEntity tile = this.world.getTileEntity(pos);
			if (tile == null)
				continue;
			if (tile instanceof IAcceptMagic)
				return new TargetInfo(pos, (IAcceptMagic) tile);
			return null;
		}
		return null;
	}

	public boolean isTorch(EnumFacing facing) {
		IBlockState state = this.world.getBlockState(pos.offset(facing));
		return state.getBlock() == ESInitInstance.BLOCKS.MAGIC_TORCH
				&& state.getValue(BlockMagicTorch.FACING) == facing;
	}

	public void torch(EnumFacing facing, boolean open) {
		if (!this.isTorch(facing))
			return;
		BlockPos pos = this.pos.offset(facing);
		IBlockState state = this.world.getBlockState(pos);
		if (state.getValue(BlockMagicTorch.LIT) == open)
			return;
		this.world.setBlockState(pos, state.withProperty(BlockMagicTorch.LIT, open));
	}

	/** 当这个tile被放下，开始寻找四周的accpter */
	public void coming() {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			if (this.isTorch(facing))
				this.find(facing, true);
			else {
				// 没有火把的情况下，由于是第一出来，也会强行说hi
				TargetInfo info = this.findTarget(this.pos, facing, this.getDistance());
				if (info != null) {
					TileMDBase tile = info.to(TileMDBase.class);
					if (tile != null)
						tile.hi(this.pos, facing.getOpposite());
				}
			}
		}
	}

	/** 当这个tile离开 ,应该在自身已经移除世界后调用 */
	public void leaving(BlockPos pos) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			TargetInfo info = this.findTarget(pos, facing, this.getDistance());
			if (info != null) {
				TileMDBase tile = info.to(TileMDBase.class);
				if (tile != null)
					tile.bye(pos, facing.getOpposite());
			}
		}
	}

	/** 当指定方向的方块改变了 */
	public void change(EnumFacing facing) {
		if (facing.getHorizontalIndex() < 0)
			return;
		this.find(facing, true);
	}

	/** 寻找并连接到 */
	public void find(EnumFacing facing, boolean sayHi) {
		int index = facing.getIndex();
		// 没有火把的时候
		if (!this.isTorch(facing)) {
			targets[index] = null;
			this.markDirty();
			return;
		}
		// 寻找目标
		targets[index] = this.findTarget(this.pos, facing, this.getDistance());
		if (targets[index] != null && sayHi) {
			TileMDBase tile = targets[index].to(TileMDBase.class);
			if (tile != null)
				tile.hi(this.pos, facing.getOpposite());
		}
		if (targets[index] == null)
			this.torch(facing, false);
		this.markDirty();
	}

	/** 其他tile找到自己的时候 */
	public void hi(BlockPos from, EnumFacing facing) {
		this.find(facing, false);
		int index = facing.getIndex();
		if (targets[index] != null && targets[index].pos.equals(from)) {
			TileMDBase tile = targets[index].to(TileMDBase.class);
			if (tile != null)
				tile.oh(this.pos, facing.getOpposite());
		}
	}

	/** 打招呼后，对面tile也选定了自己 */
	public void oh(BlockPos from, EnumFacing facing) {

	}

	/** 其他方块不再向自己传输时候 */
	public void bye(BlockPos from, EnumFacing facing) {
		this.find(facing, true);
		int index = facing.getIndex();
		if (targets[index] == null)
			this.torch(facing, false);
	}

	/** 检查所有位置状态 */
	public void checkAll() {
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == null)
				continue;
			if (!targets[i].check()) {
				this.find(EnumFacing.getFront(i), true);
			}
		}
	}

	/** 进行一次传输 */
	protected void transferOnce() {
		if (this.getCurrentCapacity() < this.getOverflow())
			return;
		int sendCount = this.getMaxSendCountOnce();
		sendCount = Math.min(sendCount, this.magic.getCount());
		ElementStack sendMagic = this.magic.splitStack(sendCount);
		if (sendCount <= 0)
			return;
		// 总共能量
		int sumLevel = 0;
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == null)
				continue;
			sumLevel += targets[i].lev;
		}
		// 发送
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == null)
				continue;
			EnumFacing facing = EnumFacing.getFront(i);
			if (sendMagic.isEmpty()) {
				this.torch(facing, false);
				continue;
			} else {
				this.torch(facing, true);
			}
			int count = targets[i].lev * sendCount / sumLevel;
			count = count == 0 ? 1 : count;
			ElementStack send = sendMagic.splitStack(count);
			ElementStack remain = targets[i].accpetMagic(send, this.pos, facing.getOpposite());
		}
		this.markDirty();
	}

	/** 进行一次传输 */
	@SideOnly(Side.CLIENT)
	protected void transferClientEffect() {
		for (int i = 0; i < targets.length; i++) {
			EnumFacing facing = EnumFacing.getFront(i);
			if (this.isTorch(facing)) {
				if (this.world.getBlockState(pos.offset(facing)).getValue(BlockMagicTorch.LIT)) {
					// 客户端和服务器并没有同步这些数据，想要展示效果需要重新找
					if (targets[i] == null)
						this.find(facing, false);
					else if (!targets[i].check()) {
						this.find(facing, false);
						// 这种情况应该不存在
						if (targets[i] == null)
							continue;
						this.effectTo(targets[i].pos);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void effectTo(BlockPos to) {

	}

	/** 获取距离 */
	protected int getDistance() {
		return 16;
	}

	/** 获取每次送的最大数量 */
	protected abstract int getMaxSendCountOnce();

	/** 获取达到多少值就可以向外发送了，超过了就会发送 */
	protected abstract int getOverflow();

	@Override
	public ElementStack accpetMagic(ElementStack magic, BlockPos from, EnumFacing facing) {
		int rest = this.getMaxCapacity() - this.getCurrentCapacity();
		rest = Math.min(magic.getCount(), rest);
		this.magic.grow(magic.splitStack(rest));
		return magic;
	}

	@Override
	public int getCurrentCapacity() {
		return magic.getCount();
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
}
