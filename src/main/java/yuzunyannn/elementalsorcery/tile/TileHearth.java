package yuzunyannn.elementalsorcery.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ability.IGetBurnPower;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.IField;

public class TileHearth extends TileEntityNetwork implements ITickable, IField {

	// 剩余燃烧时间
	protected int burnTime = 0;
	// 这次开始的时间
	protected int totalBurnTime = 1;
	// 是否发光
	protected boolean isBurn = false;

	// 这个仓库必须是可以燃烧的东西
	protected ItemStackHandler inventory = new ItemStackHandler(4) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (TileEntityFurnace.isItemFuel(stack))
				return super.insertItem(slot, stack, simulate);
			return stack;
		}
	};

	// 更新条件
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	// 拥有能力！
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == EnumFacing.UP)
				return super.hasCapability(capability, facing);
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	// 获取能力！
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == EnumFacing.UP)
				return super.getCapability(capability, facing);
			return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}

	// 加载
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
		this.burnTime = compound.getInteger("BurnTime");
		this.totalBurnTime = compound.getInteger("TotalBurnTime");
	}

	// 保存
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("Inventory", this.inventory.serializeNBT());
		compound.setInteger("BurnTime", this.burnTime);
		compound.setInteger("TotalBurnTime", this.totalBurnTime);
		return super.writeToNBT(compound);
	}

	public static boolean giveBurnPower(TileEntity tile, int amount, int level) {
		IGetBurnPower receiver = null;
		if (tile instanceof IGetBurnPower)
			receiver = (IGetBurnPower) tile;
		if (receiver == null)
			return false;
		if (level <= 0) {
			amount = 0;
			level = 0;
		}
		return receiver.receiveBurnPower(amount, level);
	}

	private void brunIt(boolean burn) {
		IBlockState state = world.getBlockState(pos);
		// if (state.getBlock() != ESInitInstance.BLOCKS.HEARTH)return;
		world.setBlockState(pos, state.withProperty(BlockHearth.BURNING, burn));
	}

	public static int getBurnLevel(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() != ESInitInstance.BLOCKS.HEARTH)
			return 0;
		BlockHearth.EnumMaterial material = state.getValue(BlockHearth.MATERIAL);
		int level = 0;
		switch (material) {
		case COBBLESTONE:
			level = 1;
			break;
		case IRON:
			level = 2;
			break;
		case KYANITE:
			level = 3;
			break;
		default:
			break;
		}
		return level;
	}

	public int getBurnLevel() {
		return getBurnLevel(world, pos);
	}

	@Override
	public void update() {
		// 获取楼上的TileEntity
		TileEntity tile = world.getTileEntity(pos.up());
		// 如果燃烧还有剩余时间
		if (burnTime > 0) {
			burnTime--;
			// 给予楼上方块燃烧能量
			giveBurnPower(tile, 1, this.getBurnLevel());
			if (burnTime == 0 && !world.isRemote) {
				this.markDirty();
				brunIt(false);
			} else
				return;
		}
		// 检查楼上是否能接受热量，不能的话，就不消耗一个新物品了
		if (!giveBurnPower(tile, 0, 0))
			return;
		// 假装取出来个
		ItemStack item_stack = ItemStack.EMPTY;
		int slot;
		for (slot = 0; slot < inventory.getSlots(); slot++) {
			item_stack = inventory.extractItem(slot, 1, true);
			if (!item_stack.isEmpty())
				break;
		}
		if (item_stack.isEmpty())
			return;
		// 重置燃烧时间
		burnTime = TileEntityFurnace.getItemBurnTime(item_stack);
		if (burnTime <= 0)
			burnTime = 1;
		totalBurnTime = burnTime;
		if (world.isRemote)
			return;
		// 消耗掉这个物品
		inventory.extractItem(slot, 1, false);
		brunIt(true);
		this.markDirty();
	}

	public static final int FIELD_TOTAL_BURN_TIME = 0;
	public static final int FIELD_BURN_TIME = 1;

	@Override
	public int getField(int id) {
		switch (id) {
		case FIELD_TOTAL_BURN_TIME:
			return totalBurnTime;
		case FIELD_BURN_TIME:
			return burnTime;
		}
		return -1;
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case FIELD_TOTAL_BURN_TIME:
			totalBurnTime = value;
			break;
		case FIELD_BURN_TIME:
			burnTime = value;
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 2;
	}

	// 获取对应方块的名字
	public String getBlockUnlocalizedName() {
		IBlockState state = world.getBlockState(pos);
		return BlockHearth.metaToUnlocalizedName(state.getBlock().damageDropped(state));
	}

}
