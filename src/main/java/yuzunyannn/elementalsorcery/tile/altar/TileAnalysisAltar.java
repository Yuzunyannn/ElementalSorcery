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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ability.IGetItemStack;
import yuzunyannn.elementalsorcery.api.ability.IItemStructure;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.ElementHelper;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

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

	public boolean isOk() {
		return this.ok;
	}

	int powerTime;

	@Override
	public void update() {
		if (!this.isIntact()) {
			daStack = ItemStack.EMPTY;
			this.stateClear();
			return;
		}
		if (checkTime % 10 == 0) {
			// 检查物品
			ItemStack stack = this.getStackToAnalysis();
			if (stack != this.daStack) {
				this.daStack = stack;
				if (this.daStack.isEmpty())
					this.stateClear();
			}
		}
		if (this.daStack.isEmpty())
			return;
		if (this.daEstacks == null && checkTime % 40 == 0) {
			// 解析元素
			this.daEstacks = ElementMap.instance.toElement(this.daStack);
			if (this.daEstacks == null) {
				this.stateClear();
				this.daEstacks = EMPTY_ESTACKS;
			} else {
				this.daComplex = ElementMap.instance.complex(this.daStack);
				ItemStack remain = this.daStack;
				int rest = 1;
				do {
					remain = ElementMap.instance.remain(remain);
					if (remain.isEmpty())
						break;
					if (rest <= 0) {
						// 到达限制无法继续解析
						this.stateClear();
						this.daEstacks = EMPTY_ESTACKS;
						break;
					}
					ElementStack[] remainStacks = ElementMap.instance.toElement(remain);
					if (remainStacks != null) {
						this.daEstacks = ElementHelper.merge(this.daEstacks, remainStacks);
						this.daComplex = this.daComplex + ElementMap.instance.complex(remain);
					}
					rest--;
				} while (true);
			}
		}
		if (this.world.isRemote)
			return;
		// 结构数据写入水晶
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack.isEmpty()) {
			this.powerTime = 0;
			return;
		}
		if (this.daEstacks == null || this.daEstacks.length == 0) {
			this.powerTime = 0;
			return;
		}
		IItemStructure structure = ItemStructure.getItemStructure(stack);
		if (ItemHelper.areItemsEqual(structure.getStructureItem(0), this.daStack))
			return;
		this.powerTime++;
		if (this.powerTime >= this.getTotalPowerTime()) {
			this.powerTime = 0;
			structure.set(0, this.daStack, this.daComplex, this.daEstacks);
			structure.saveState(stack);
			this.markDirty();
		}
	}

	private void stateClear() {
		this.daEstacks = null;
		this.daComplex = 0;
		this.powerTime = 0;
	}

	private ItemStack getStackToAnalysis() {
		BlockPos pos = this.pos.down(3);
		TileEntity tile = this.world.getTileEntity(pos);
		if (tile instanceof IGetItemStack) {
			return ((IGetItemStack) tile).getStack();
		}
		return ItemStack.EMPTY;
	}

	public int getPowerTime() {
		return this.powerTime;
	}

	@SideOnly(Side.CLIENT)
	public void setPowerTime(int i) {
		this.powerTime = i;
	}

	public int getTotalPowerTime() {
		return 20 * 30;
	}

}
