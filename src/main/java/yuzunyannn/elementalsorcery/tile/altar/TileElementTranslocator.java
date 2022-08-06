package yuzunyannn.elementalsorcery.tile.altar;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementStackDouble;

public class TileElementTranslocator extends TileEntityNetwork {

	protected ItemStackHandler inventory = new ItemStackHandler(2) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (!stack.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null)) return false;
			return true;
		};
	};

	protected ElementStackDouble edstack = new ElementStackDouble();

	public ItemStackHandler getItemStackHandler() {
		return inventory;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		nbtReadItemStackHanlder(compound, "inv", inventory);
		edstack = new ElementStackDouble(compound.getCompoundTag("element"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		nbtWriteItemStackHanlder(compound, "inv", inventory);
		if (!edstack.isEmpty()) compound.setTag("element", edstack.serializeNBT());
		return super.writeToNBT(compound);
	}

	public void setElementStack(ElementStack estack) {
		edstack.become(estack);
	}

	public ElementStack getElementStack() {
		return edstack.toElementStack();
	}

	public boolean isElementEmpty() {
		return edstack.isEmpty();
	}

	public int toElementColor() {
		return edstack.getColor();
	}

	public boolean mergeElement(ElementStack src) {
		return edstack.mergeElement(src);
	}

	public boolean doTransferInput(int count) {
		if (count <= 0) return false;

		ItemStack stack = inventory.getStackInSlot(0);
		if (stack.isEmpty()) return false;
		IElementInventory einv = ElementHelper.getElementInventory(stack);
		if (einv == null) return false;

		ElementStack estack = ElementStack.EMPTY;
		for (int i = 0; i < einv.getSlots(); i++) {
			estack = einv.getStackInSlot(i);
			if (!estack.isEmpty()) break;
		}
		if (estack.isEmpty()) return false;

		count = Math.min(count, estack.getCount());
		ElementStack src = estack.copy().splitStack(count);
		src = einv.extractElement(src, true);
		if (src.isEmpty()) return false;

		if (!mergeElement(src)) return false;

		einv.extractElement(src, false);
		einv.saveState(stack);

		this.markDirty();
		return true;
	}

	public boolean doTransferOutput(int count) {
		if (count <= 0) return false;

		if (isElementEmpty()) return false;

		ItemStack stack = inventory.getStackInSlot(1);
		if (stack.isEmpty()) return false;
		IElementInventory einv = ElementHelper.getElementInventory(stack);
		if (einv == null) return false;

		count = Math.min(count, (int) edstack.getCount());

		ElementStack genStack = getElementStack();
		genStack.setCount(count);

		if (!einv.insertElement(genStack, false)) return false;

		edstack.setCount(edstack.getCount() - count);
		einv.saveState(stack);

		this.markDirty();
		return true;
	}
}
