package yuzunyannn.elementalsorcery.tile.altar;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ability.IGetItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;

public class TileAnalysisAltar extends TileStaticMultiBlock implements ITickable {

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.ANALYSIS_ALTAR, this, new BlockPos(0, -4, 0));
	}

	// 仓库，存放结构石头
	protected ItemStackHandler inventory = new ItemStackHandler() {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (ItemStructure.canStorageItemStructure(stack))
				return super.insertItem(slot, stack, simulate);
			return stack;
		}
	};

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("inventory", this.inventory.serializeNBT());
		return super.writeToNBT(nbt);
	}

	ItemStack daStack = ItemStack.EMPTY;
	ElementStack[] daEstacks = null;
	int daComplex = 0;
	public static final ElementStack[] EMPTY_ESTACKS = new ElementStack[0];

	public ItemStack getDAStack() {
		return daStack;
	}

	public ElementStack[] getDAEstacks() {
		return daEstacks;
	}

	public int getDAComplex() {
		return daComplex;
	}

	@Override
	public void update() {
		if (!this.isIntact())
			return;
		checkTime++;
		if (checkTime % 10 == 0) {
			this.daStack = this.getStackToAnalysis();
			if (this.daStack.isEmpty()) {
				this.daEstacks = null;
			}
		}
		if (this.daStack.isEmpty())
			return;
		if (checkTime % 40 == 0) {
			this.daEstacks = ElementMap.instance.toElement(this.daStack);
			if (this.daEstacks == null) {
				this.daEstacks = EMPTY_ESTACKS;
				this.daComplex = 0;
			} else {
				this.daComplex = ElementMap.instance.complex(this.daStack);
			}
		}
	}

	private ItemStack getStackToAnalysis() {
		BlockPos pos = this.pos.down(3);
		TileEntity tile = this.world.getTileEntity(pos);
		if (tile instanceof IGetItemStack) {
			return ((IGetItemStack) tile).getStack();
		}
		return ItemStack.EMPTY;
	}

}
