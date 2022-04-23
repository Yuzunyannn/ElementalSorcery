package yuzunyannn.elementalsorcery.tile;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.item.IPlatformTickable;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.api.util.WorldObjectTileEntity;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryOnlyInsert;

public class TileElementPlatform extends TileEntityNetwork implements IGetItemStack, ITickable, IAltarWake {

	// 只能存不能取
	protected ElementInventoryOnlyInsert inventory = new ElementInventoryOnlyInsert(4);
	protected ItemStack stack = ItemStack.EMPTY;
	protected NBTTagCompound runData = null;
	protected int tick;
	protected boolean needSyncEInv;

	final public WorldObjectTileEntity caster = new WorldObjectTileEntity(this);

	@Override
	public void setStack(ItemStack stack) {
		if (this.stack == stack) return;
		this.stack = stack;

		if (this.stack.isEmpty()) runData = null;
		else runData = new NBTTagCompound();
		tick = 0;

		this.updateToClient();
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return this.stack;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		stack = nbtReadItemStack(nbt, "stack");
		if (nbt.hasKey("runData", NBTTag.TAG_COMPOUND)) runData = nbt.getCompoundTag("runData");
		if (this.isSending()) {
			if (inventory.hasState(nbt)) inventory.loadState(nbt);
			return;
		} else runData = null;
		inventory.loadState(nbt);
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbtWriteItemStack(nbt, "stack", stack);
		if (runData != null) nbt.setTag("runData", runData);
		if (this.isSending()) {
			if (needSyncEInv) inventory.saveState(nbt);
			return nbt;
		}
		inventory.saveState(nbt);
		return super.writeToNBT(nbt);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return (T) inventory;
		return super.getCapability(capability, facing);
	}

	@Override
	public void update() {
		if (stack.isEmpty()) return;
		Item item = stack.getItem();
		tick++;
		if (item instanceof IPlatformTickable) {
			inventory.setForbid(false);
			try {
				boolean needUpdate = ((IPlatformTickable) item).platformUpdate(world, stack, caster, runData, tick);
				if (needUpdate || stack.isEmpty()) {
					this.markDirty();
					this.updateToClient();
				}
			} catch (Exception e) {
				ElementalSorcery.logger.warn("TileElementPlatform tick error", e);
				this.setStack(ItemStack.EMPTY);
			}
			inventory.setForbid(true);
		} else {
			if (world.isRemote) return;
			if (tick % 20 != 0) return;
			// 转移仓库
			IElementInventory dstInv = ElementHelper.getElementInventory(stack);
			if (dstInv == null) return;
			boolean hasChange = false;
			for (int i = 0; i < inventory.getSlots(); i++) {
				ElementStack srcStack = inventory.getStackInSlot(i);
				if (srcStack.isEmpty()) continue;
				ElementStack splitStack = srcStack.splitStack(10);
				if (dstInv.insertElement(splitStack, false)) hasChange = true;
				else srcStack.grow(splitStack);
			}
			if (hasChange) {
				this.markDirty();
				dstInv.saveState(stack);
			}
		}
	}

	@Override
	public boolean wake(int type, BlockPos from) {
		return type == OBTAIN;
	}

	@Override
	public void onInventoryStatusChange() {
		needSyncEInv = true;
		updateToClient();
		needSyncEInv = false;
	}

}
