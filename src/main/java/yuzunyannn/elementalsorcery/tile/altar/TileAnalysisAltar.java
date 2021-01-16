package yuzunyannn.elementalsorcery.tile.altar;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.api.tile.IItemStructureCraft;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TileAnalysisAltar extends TileStaticMultiBlock implements ITickable {

	protected MultiBlock addition;

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.ANALYSIS_ALTAR, this, new BlockPos(0, -4, 0));
		addition = new MultiBlock(Buildings.ANALYSIS_ALTAR_ADD, this, new BlockPos(0, 0, 0));
	}

	// 仓库，存放结构石头
	protected ItemStackHandler inventory = new ItemStackHandler() {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (ItemStructure.canStorageItemStructure(stack)) return super.insertItem(slot, stack, simulate);
			return stack;
		}
	};

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) { return true; }
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) { return (T) inventory; }
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

	public static final ElementStack[] EMPTY_ESTACKS = new ElementStack[0];

	public static class AnalysisPacket implements INBTSerializable<NBTTagCompound> {
		public ElementStack[] daEstacks = null;
		public ItemStack daStack = ItemStack.EMPTY;
		public int daComplex = 0;

		public AnalysisPacket() {

		}

		public AnalysisPacket(NBTTagCompound nbt) {
			this.deserializeNBT(nbt);
		}

		public void merge(AnalysisPacket other) {
			this.daEstacks = ElementHelper.merge(this.daEstacks, other.daEstacks);
			this.daComplex = this.daComplex + other.daComplex;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("complex", daComplex);
			nbt.setTag("item", daStack.serializeNBT());
			nbt.setTag("einv", new ElementInventory(daEstacks).serializeNBT());
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			daComplex = nbt.getInteger("complex");
			daStack = new ItemStack(nbt.getCompoundTag("item"));
			daEstacks = new ElementInventory(nbt.getCompoundTag("einv")).getEStacksAndClear();
		}
	}

	protected IItemStructureCraft structureCraft = null;
	protected AnalysisPacket ans = null;
	protected boolean cannotAnalysis = false;

	public AnalysisPacket getAnalysisPacket() {
		return ans;
	}

	@SideOnly(Side.CLIENT)
	public void setAnalysisPacket(NBTTagCompound nbt) {
		if (nbt == null) this.ans = null;
		else this.ans = new AnalysisPacket(nbt);
	}

	public ItemStack getDAStack() {
		return ans == null ? ItemStack.EMPTY : ans.daStack;
	}

	// 分析的元素
	public ElementStack[] getDAEstacks() {
		return ans == null ? null : ans.daEstacks;
	}

	public int getDAComplex() {
		return ans == null ? 0 : ans.daComplex;
	}

	public boolean cannotAnalysis() {
		return cannotAnalysis;
	}

	@SideOnly(Side.CLIENT)
	public void setCannotAnalysis() {
		this.ans = new AnalysisPacket();
		this.ans.daStack = this.getStackToAnalysis();
		this.ans.daEstacks = new ElementStack[0];
	}

	public boolean isOk() {
		return this.ok;
	}

	int powerTime;

	@Override
	public void update() {
		// 检查是否完整
		if (!this.isIntact()) {
			this.stateClear();
			return;
		}
		if (world.isRemote) return;
		if (checkTime % 40 == 0) {
			// 检测是否存在合成解析
			structureCraft = this.getCraftToAnalysis();
			if (structureCraft != null) {
				// 处理递归数据
				ItemStack stack = structureCraft.getOutput();
				if (!ItemHelper.areItemsEqual(stack, this.getDAStack())) {
					this.stateClear();
					if (stack.isEmpty()) {
						cannotAnalysis = false;
						return;
					}
					// 试图直接解析
					this.ans = analysisItem(stack, ElementMap.instance, true);
					// 没法直接解析？进行递归搜索
					if (this.ans == null) this.updateItemStructure(stack);
					cannotAnalysis = this.ans == null;
				}
			} else {
				// 检查物品是否存在，更换
				ItemStack stack = this.getStackToAnalysis();
				if (stack != this.getDAStack()) {
					this.stateClear();
					this.ans = analysisItem(stack, ElementMap.instance, true);
					cannotAnalysis = this.ans == null;
				}
			}
		}
		this.writeToItem(ans);
	}

	/** 处理结构水晶的结果 */
	private void updateItemStructure(ItemStack output) {
		List<ItemStack> inputs = structureCraft.getInputs();
		if (inputs != null && !inputs.isEmpty()) {
			IToElement elementMap = this.getElementMapSample();
			for (ItemStack input : inputs) {
				if (input.isEmpty()) continue;
				AnalysisPacket ans = analysisItem(input, elementMap, structureCraft.calcRemain(input));
				// 存在任何一个找不到的，直接清除
				if (ans == null) {
					this.stateClear();
					return;
				}
				if (this.ans == null) this.ans = ans;
				else this.ans.merge(ans);
			}
			// 重置真正的物品
			if (this.ans != null) {
				this.ans.daStack = output.copy();
				int n = this.ans.daStack.getCount();
				// 处理多个物品
				if (n > 1) {
					this.ans.daStack.setCount(1);
					for (ElementStack estack : this.ans.daEstacks) {
						int count = estack.getCount() / n;
						estack.setCount(Math.max(count, 1));
					}
				}
			}
		}
	}

	private void writeToItem(AnalysisPacket ans) {
		if (this.world.isRemote) return;
		if (ans == null) return;
		// 结构数据写入水晶
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack.isEmpty()) {
			this.powerTime = 0;
			return;
		}
		IItemStructure structure = ItemStructure.getItemStructure(stack);
		// 是否满了
		if (structure.getItemCount() >= structure.getItemMaxCount()) {
			this.powerTime = 0;
			return;
		}
		// 是否有需要插入的
		if (structure.hasItem(ans.daStack)) {
			this.powerTime = 0;
			return;
		}
		this.powerTime++;
		if (this.powerTime >= this.getTotalPowerTime()) {
			this.powerTime = 0;
			// 进行新的数据插入
			structure.add(ans.daStack, ans.daComplex, ans.daEstacks);
			structure.saveState(stack);
			this.markDirty();
		}
	}

	public static AnalysisPacket analysisItem(ItemStack stack, IToElement elementMap, boolean needRemian) {
		AnalysisPacket ans = new AnalysisPacket();
		ans.daStack = stack;
		// 解析元素
		ans.daEstacks = ElementHelper.copy(elementMap.toElement(stack));
		if (ans.daEstacks == null) return null;
		ans.daComplex = elementMap.complex(stack);
		if (!needRemian) return ans;
		ItemStack remain = stack;
		int rest = 1;
		do {
			remain = elementMap.remain(remain);
			if (remain.isEmpty()) break;
			if (rest <= 0) return null;
			ElementStack[] remainStacks = ElementHelper.copy(elementMap.toElement(remain));
			if (remainStacks != null) {
				ans.daEstacks = ElementHelper.merge(ans.daEstacks, remainStacks);
				ans.daComplex = ans.daComplex + elementMap.complex(remain);
			}
			rest--;
		} while (true);
		return ans;
	}

	private void stateClear() {
		this.powerTime = 0;
		this.ans = null;
	}

	/** 获取解析的物品 */
	protected ItemStack getStackToAnalysis() {
		BlockPos pos = this.pos.down(3);
		TileEntity tile = this.world.getTileEntity(pos);
		if (tile instanceof IGetItemStack) return ((IGetItemStack) tile).getStack();
		return ItemStack.EMPTY;
	}

	public int getPowerTime() {
		return this.powerTime;
	}

	/** 获取合成分析的物品 */
	protected IItemStructureCraft getCraftToAnalysis() {
		BlockPos pos = this.pos.down(3);
		TileEntity tile = this.world.getTileEntity(pos);
		if (tile instanceof IItemStructureCraft) return (IItemStructureCraft) tile;
		return null;
	}

	/** 获取所有附加的结构水晶 */
	protected IToElement getElementMapSample() {
		ElementMap em = new ElementMap();
		em.add(ElementMap.instance);
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			for (int n = 4; n <= 7; n++) {
				BlockPos pos = this.pos.offset(facing, n);
				IItemHandler inv = BlockHelper.getItemHandler(world, pos, facing.getOpposite());
				if (inv != null) {
					addition.moveTo(pos.down(4));
					if (addition.check(EnumFacing.NORTH)) {
						// 查找记录元素数据的物品（物品水晶）
						for (int i = 0; i < inv.getSlots(); i++) {
							ItemStack stack = inv.getStackInSlot(i);
							if (stack.isEmpty()) continue;
							IItemStructure structure = ItemStructure.getItemStructure(stack);
							if (structure.isEmpty()) continue;
							em.add(structure);
						}
					}
				}
			}
		}
		return em;
	}

	@SideOnly(Side.CLIENT)
	public void setPowerTime(int i) {
		this.powerTime = i;
	}

	public int getTotalPowerTime() {
		return 20 * 30;
	}

}
