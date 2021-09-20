package yuzunyannn.elementalsorcery.tile.altar;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class TileElementTranslocator extends TileEntityNetwork {

	protected ItemStackHandler inventory = new ItemStackHandler(2) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (!stack.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null)) return false;
			return true;
		};
	};

	protected Element element = ESInit.ELEMENTS.VOID;
	protected double count = 0;
	protected double power = 0;

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
		if (compound.hasKey("element", NBTTag.TAG_COMPOUND)) loadElement: {
			NBTTagCompound eNBT = compound.getCompoundTag("element");
			ResourceLocation id = new ResourceLocation(eNBT.getString("id"));
			Element element = Element.getElementFromName(id);
			if (element == null) break loadElement;
			this.element = element;
			this.count = eNBT.getFloat("size");
			this.power = eNBT.getFloat("power");
		}
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		nbtWriteItemStackHanlder(compound, "inv", inventory);
		if (!isElementEmpty()) saveElement: {
			NBTTagCompound eNBT = new NBTTagCompound();
			ResourceLocation id = Element.getNameFromElement(this.element);
			if (id == null) break saveElement;
			eNBT.setString("id", id.toString());
			eNBT.setFloat("size", (float) count);
			eNBT.setFloat("power", (float) power);
			compound.setTag("element", eNBT);
		}
		return super.writeToNBT(compound);
	}

	public void setElementStack(ElementStack estack) {
		this.element = estack.getElement();
		this.count = estack.getCount();
		this.power = estack.getPower();
	}

	public ElementStack getElementStack() {
		return new ElementStack(element, (int) count, (int) power);
	}

	public boolean isElementEmpty() {
		return element == ESInit.ELEMENTS.VOID || count <= 0 || power <= 0;
	}

	public int getElementColor() {
		return element.getColor(getElementStack());
	}

	public boolean mergeElement(ElementStack src) {
		if (src.getElement() != element) return false;

		double count = this.count + src.getCount();

		double weightDst = this.count;
		double weightSrc = src.getCount();

		if (this.power > src.getPower()) weightDst = weightDst + 10;
		else weightSrc = weightSrc + 10;

		double power = (this.power * weightDst + src.getPower() * weightSrc) / (weightSrc + weightDst);

		this.count = count;
		this.power = power;

		return true;
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

		if (isElementEmpty()) {
			this.element = src.getElement();
			this.count = src.getCount();
			this.power = estack.getPower();
		} else {
			if (!mergeElement(src)) return false;
		}

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

		count = Math.min(count, (int) this.count);

		ElementStack genStack = getElementStack();
		genStack.setCount(count);

		if (!einv.insertElement(genStack, false)) return false;

		this.count = this.count - count;
		einv.saveState(stack);

		this.markDirty();
		return true;
	}
}
