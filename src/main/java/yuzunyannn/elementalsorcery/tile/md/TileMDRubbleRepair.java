package yuzunyannn.elementalsorcery.tile.md;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.block.BlocksAStone;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.particle.FirwrokShap;

public class TileMDRubbleRepair extends TileMDBase implements ITickable {

	/** 完成度 */
	protected int complete;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("complete", complete);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		complete = nbt.getInteger("complete");
		super.readFromNBT(nbt);
	}

	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(1) {
			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (TileMDRubbleRepair.getDefaultRepairResult(stack).isEmpty()) return stack;
				return super.insertItem(slot, stack, simulate);
			}
		};
	}

	/** 获取物品 */
	public ItemStack getStack() {
		return inventory.getStackInSlot(0);
	}

	/** 设置物品 */
	public void setStack(Block block) {
		int count = this.getStack().getCount();
		this.inventory.setStackInSlot(0, new ItemStack(block, count == 0 ? 1 : count, isStart ? 1 : 0));
	}

	public int getComplete() {
		return complete;
	}

	public int getTotalComplete() {
		return this.getStack().isEmpty() ? 1 : this.getStack().getCount() * 16;
	}

	int lastId;
	ItemStack lastStack = ItemStack.EMPTY;
	boolean isStart = false;

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
		case 1:
			return super.getField(id);
		case 2:
			return isStart ? 1 : 0;
		case 3:
			return complete;
		default:
			return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
		case 1:
			super.setField(id, value);
			break;
		case 2:
			isStart = value == 0 ? false : true;
			this.setStack(ESInitInstance.BLOCKS.ASTONE);
			break;
		case 3:
			this.complete = value;
			break;
		default:
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 5;
	}

	NBTTagCompound sndTemp = new NBTTagCompound();

	protected void detectAndSendItemType() {
		if (this.detectAndWriteToNBT(sndTemp, 2)) this.updateToClient(sndTemp);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	/** 获取默认的修复结果 */
	public static ItemStack getDefaultRepairResult(ItemStack stack) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block == Blocks.AIR) return ItemStack.EMPTY;
		else if (block == Blocks.COBBLESTONE) return new ItemStack(Blocks.STONE);
		else if (block == ESInitInstance.BLOCKS.ASTONE
				&& stack.getMetadata() == BlocksAStone.EnumType.FRAGMENTED.ordinal())
			return new ItemStack(ESInitInstance.BLOCKS.ASTONE);
		else if (block == Blocks.STONEBRICK && stack.getMetadata() == 2) return new ItemStack(Blocks.STONEBRICK);
		return ItemStack.EMPTY;
	}

	/** 获取修复花费 */
	public static int getDefaultRepairCost(ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block == Blocks.AIR) return 0;
		else if (block == Blocks.COBBLESTONE) return 1;
		else if (block == ESInitInstance.BLOCKS.ASTONE
				&& stack.getMetadata() == BlocksAStone.EnumType.FRAGMENTED.ordinal())
			return 25;
		else if (block == Blocks.STONEBRICK && stack.getMetadata() == 2) return 1;
		return 0;
	}

	@Override
	public void update() {
		this.autoTransfer();
		if (this.world.isRemote) {
			if (this.isStart && Math.random() < 0.2f) {
				float x = this.pos.getX() + 0.2f + (float) Math.random() * 0.6f;
				float y = this.pos.getY() + 0.1f;
				float z = this.pos.getZ() + 0.2f + (float) Math.random() * 0.6f;
				FirwrokShap.createSpark(world, x, y, z, 0, (float) Math.random() * 0.04f + 0.08f, 0, PARTICLE_COLOR,
						PARTICLE_COLOR_FADE, false, false);
			}
			return;
		}
		this.detectAndSendItemType();
		ItemStack stack = this.getStack();
		ItemStack result = TileMDRubbleRepair.getDefaultRepairResult(stack);
		if (result.isEmpty()) {
			this.complete = 0;
			this.isStart = false;
			return;
		}
		int cost = TileMDRubbleRepair.getDefaultRepairCost(stack);
		int need = cost * stack.getCount();
		if (need > this.getCurrentCapacity()) {
			this.complete = 0;
			this.isStart = false;
			return;
		}
		this.isStart = true;
		this.complete++;
		if (this.complete >= this.getTotalComplete()) {
			this.complete = 0;
			this.magic.shrink(need);
			result = result.copy();
			result.setCount(stack.getCount());
			this.inventory.setStackInSlot(0, result);
			this.markDirty();
		}
	}

}
