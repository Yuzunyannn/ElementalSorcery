package yuzunyannn.elementalsorcery.tile.md;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ability.IElementInventory;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.ElementHelper;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;

public class TileMDDeconstructBox extends TileMDBase implements ITickable {

	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(2) {
			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (slot == 0) return super.insertItem(slot, stack, simulate);
				if (stack.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, EnumFacing.NORTH))
					return super.insertItem(slot, stack, simulate);
				return stack;
			}
		};
	}

	protected MultiBlock structure;
	protected boolean ok;
	/** 积累的进度 */
	protected int progress;
	/** 当前元素颜色 */
	protected int color;

	@Override
	public void onLoad() {
		structure = new MultiBlock(Buildings.DECONSTRUCT_BOX, this, new BlockPos(0, -1, 0));
	}

	@SideOnly(Side.CLIENT)
	public int getColor() {
		return color;
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 2:
			return progress;
		case 3:
			return color;
		default:
			return super.getField(id);
		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 2:
			progress = value;
			break;
		case 3:
			color = value;
			break;
		default:
			super.setField(id, value);
			break;
		}
	}

	public int getProgress() {
		return progress;
	}

	public int getMaxProgress() {
		return 200;
	}

	@Override
	public int getFieldCount() {
		return 4;
	}

	@Override
	public void update() {
		this.autoTransfer();
		if (world.isRemote) return;
		// 没有魔力的情况
		if (this.magic.isEmpty()) {
			this.progress = Math.max(this.progress - 1, 0);
			return;
		}
		int originProgress = progress;
		progress = 0;
		// 检测多方快
		if (tick % 30 == 0) ok = structure.check(EnumFacing.NORTH);
		if (ok == false) return;
		// 获取检测物品
		ItemStack stackInv = this.inventory.getStackInSlot(1);
		if (stackInv.isEmpty()) return;
		ItemStack stack = this.inventory.getStackInSlot(0);
		if (stack.isEmpty()) return;
		// 复制物品
		ItemStack originStack = stack;
		stack = originStack.copy();
		stack.setCount(1);
		// 获取检测分解物
		ElementStack[] toEstacks = ElementMap.instance.toElement(stack);
		if (toEstacks == null) return;
		// 获取检测仓库
		IElementInventory inventory = ElementHelper.getElementInventory(stackInv);
		if (!ElementHelper.canInsert(inventory)) return;
		// 检测是否可以插入
		ElementStack inserted = this.findInsert(toEstacks, stack, inventory);
		if (inserted.isEmpty()) return;
		// 开始
		this.color = inserted.getColor();
		this.progress = originProgress;
		this.progress++;
		if (this.tick % 3 == 0) this.magic.shrink(1);
		if (this.progress >= this.getMaxProgress()) {
			this.progress = 0;
			inventory.insertElement(inserted, false);
			originStack.shrink(1);
			inventory.saveState(stackInv);
			stack = ElementMap.instance.remain(stack);
			if (!stack.isEmpty()) {
				if (originStack.isEmpty()) this.inventory.setStackInSlot(0, stack);
				else Block.spawnAsEntity(world, pos.up(), stack);
			}
			this.markDirty();
		}
	}

	private ElementStack findInsert(ElementStack[] toEstacks, ItemStack stack, IElementInventory inventory) {
		for (ElementStack estack : toEstacks) {
			estack = estack.copy().becomeElementWhenDeconstruct(world, stack, ElementMap.instance.complex(stack),
					Element.DP_BOX);
			if (inventory.insertElement(estack, true)) return estack;
		}
		return ElementStack.EMPTY;
	}
}
