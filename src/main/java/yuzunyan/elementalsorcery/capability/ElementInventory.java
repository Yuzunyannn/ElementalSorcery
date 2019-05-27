package yuzunyan.elementalsorcery.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.util.ElementHelper;

public class ElementInventory implements IElementInventory, INBTSerializable<NBTTagCompound> {

	@CapabilityInject(IElementInventory.class)
	public static Capability<IElementInventory> ELEMENTINVENTORY_CAPABILITY;

	private ElementStack[] estacks;

	public ElementInventory() {
		this(1);
	}

	public ElementInventory(int slots) {
		setSlots(slots);
	}
	
	public ElementInventory(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	@Override
	public void setSlots(int slots) {
		slots = slots <= 0 ? 1 : slots;
		slots = slots > 64 ? 64 : slots;
		estacks = new ElementStack[slots];
		for (int i = 0; i < estacks.length; i++) {
			estacks[i] = ElementStack.EMPTY.copy();
		}
	}

	@Override
	public int getSlots() {
		return estacks.length;
	}

	@Override
	public ElementStack getStackInSlot(int slot) {
		return estacks[slot];
	}

	@Override
	public int getMaxSizeInSlot(int slot) {
		return 10000;
	}

	@Override
	public ElementStack setStackInSlot(int slot, ElementStack estack) {
		ElementStack old = estacks[slot];
		estacks[slot] = estack;
		return old;
	}

	@Override
	public boolean insertElement(ElementStack estack, boolean simulate) {
		for (int i = 0; i < this.getSlots(); i++) {
			if (insertElement(i, estack, simulate))
				return true;
		}
		return false;
	}

	@Override
	public ElementStack extractElement(ElementStack estack, boolean simulate) {
		if (estack.isEmpty())
			return ElementStack.EMPTY.copy();
		ElementStack ret = ElementStack.EMPTY.copy();
		ElementStack tmp = estack.copy();
		for (int i = 0; i < this.getSlots(); i++) {
			ElementStack _new = extractElement(i, tmp, simulate);
			ret.growOrBecome(_new);
			if (ret.arePowerfulAndMoreThan(estack))
				return ret;
			else
				tmp.grow(-_new.getCount());
		}
		return ret;
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
		if (estack.isEmpty())
			return true;
		ElementStack eorigin = getStackInSlot(slot);
		if (!eorigin.isEmpty() && !eorigin.areSameType(estack))
			return false;
		if (simulate)
			return true;
		eorigin.growOrBecome(estack);
		estack.setEmpty();
		return true;
	}

	@Override
	public ElementStack extractElement(int slot, @Nonnull ElementStack estack, boolean simulate) {
		if (estack.isEmpty())
			return ElementStack.EMPTY.copy();
		ElementStack eorigin = getStackInSlot(slot);
		if (!eorigin.arePowerfulThan(estack))
			return ElementStack.EMPTY.copy();
		int size = eorigin.getCount() >= estack.getCount() ? estack.getCount() : eorigin.getCount();
		ElementStack tmp = eorigin.copy();
		tmp.setCount(size);
		if (!simulate) {
			eorigin.grow(-size);
		}
		return tmp;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) Provider.storage.writeNBT(ELEMENTINVENTORY_CAPABILITY, this, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		Provider.storage.readNBT(ELEMENTINVENTORY_CAPABILITY, this, null, nbt);
	}

	// 能力保存
	public static class Storage implements Capability.IStorage<IElementInventory> {

		@Override
		public NBTBase writeNBT(Capability<IElementInventory> capability, IElementInventory instance, EnumFacing side) {
			NBTTagCompound nbt = new NBTTagCompound();
			ElementInventory inventory = (ElementInventory) instance;
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < inventory.estacks.length; i++)
				list.appendTag(inventory.estacks[i].serializeNBT());
			nbt.setTag("list", list);
			return nbt;
		}

		@Override
		public void readNBT(Capability<IElementInventory> capability, IElementInventory instance, EnumFacing side,
				NBTBase tag) {
			NBTTagCompound nbt = (NBTTagCompound) tag;
			ElementInventory inventory = (ElementInventory) instance;
			if (!nbt.hasKey("list"))
				return;
			NBTTagList list = (NBTTagList) nbt.getTag("list");
			inventory.estacks = new ElementStack[list.tagCount()];
			for (int i = 0; i < inventory.estacks.length; i++)
				inventory.estacks[i] = new ElementStack(list.getCompoundTagAt(i));
		}

	}

	// 能力提供者
	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		private IElementInventory inventory;
		protected static IStorage<IElementInventory> storage = ELEMENTINVENTORY_CAPABILITY.getStorage();

		public Provider() {
			this(null);
		}

		public Provider(IElementInventory inventory) {
			this.inventory = inventory == null ? new ElementInventory() : inventory;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return ELEMENTINVENTORY_CAPABILITY.equals(capability);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (ELEMENTINVENTORY_CAPABILITY.equals(capability))
				return (T) inventory;
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound) storage.writeNBT(ELEMENTINVENTORY_CAPABILITY, inventory, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound compound) {
			storage.readNBT(ELEMENTINVENTORY_CAPABILITY, inventory, null, compound);
		}
	}
}
