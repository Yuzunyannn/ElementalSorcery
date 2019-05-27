package yuzunyan.elementalsorcery.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyan.elementalsorcery.init.ESInitInstance;
import yuzunyan.elementalsorcery.item.ItemParchment;
import yuzunyan.elementalsorcery.item.ItemScroll;
import yuzunyan.elementalsorcery.parchment.Pages;

public class TileStela extends TileEntityNetwork {

	private EnumFacing face = EnumFacing.NORTH;

	public void setFace(EnumFacing face) {
		this.face = face;
	}

	public EnumFacing getFace() {
		return face;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		face = EnumFacing.values()[compound.getByte("face")];
		this.inv_item.deserializeNBT(compound.getCompoundTag("inv_item"));
		this.inv_paper.deserializeNBT(compound.getCompoundTag("inv_paper"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setByte("face", (byte) face.ordinal());
		compound.setTag("inv_item", this.inv_item.serializeNBT());
		compound.setTag("inv_paper", this.inv_paper.serializeNBT());
		return super.writeToNBT(compound);
	}

	protected ItemStackHandler inv_item = new ItemStackHandler(1);
	protected ItemStackHandler inv_paper = new ItemStackHandler(1) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			// 如果物品不是羊皮纸
			if (stack.getItem() != ESInitInstance.ITEMS.PARCHMENT)
				return stack;
			// 有内容的东西不能放入
			if (Pages.getPageId(stack) != 0)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}
	};

	// 拥有能力
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	// 获取能力
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == face.getOpposite())
				return (T) inv_item;
			return (T) inv_paper;
		}
		return super.getCapability(capability, facing);
	}

	// 是否正在工作
	public boolean isRunning() {
		ItemStack paper = inv_paper.extractItem(0, 1, true);
		if (paper.isEmpty())
			return false;
		ItemStack item = inv_item.extractItem(0, 1, true);
		if (item.isEmpty())
			return false;
		return canRunning();
	}

	// 检测周围的运行条件
	public boolean canRunning() {
		EnumFacing face = this.face.rotateY();
		IBlockState state = this.world.getBlockState(pos.offset(face));
		boolean haveFlower = state.getBlock() instanceof BlockFlower;
		state = this.world.getBlockState(pos.offset(face.getOpposite()));
		haveFlower &= state.getBlock() instanceof BlockFlower;
		return haveFlower;
	}

	// 工作一次
	public void doOnce() {
		if (!this.isRunning())
			return;
		ItemStack item = inv_item.extractItem(0, 1, true);
		int[] idArray = TileStela.pageAwareFromItem(item);
		if (idArray.length == 0) {
			return;
		}
		ItemStack paper = inv_paper.extractItem(0, idArray.length, false);
		if (idArray.length == 1) {
			Block.spawnAsEntity(this.world, this.pos, ItemParchment.getParchment(idArray[0]));
			return;
		}
		int size = Math.min(paper.getCount(), idArray.length);
		int[] ids = new int[size];
		for (int i = 0; i < size; i++)
			ids[i] = idArray[i];
		Block.spawnAsEntity(this.world, this.pos, ItemScroll.getScroll(ids));
	}

	static public int[] pageAwareFromItem(ItemStack stack) {
		return new int[0];
	}
}
