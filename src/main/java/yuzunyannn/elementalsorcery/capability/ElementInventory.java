package yuzunyannn.elementalsorcery.capability;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IElementInventoryModifiable;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.util.ContainerArrayDetecter;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class ElementInventory implements IElementInventoryModifiable, INBTSerializable<NBTTagCompound>,
		ContainerArrayDetecter.ICanArrayDetected<ElementStack, NBTTagIntArray> {

	@CapabilityInject(IElementInventory.class)
	public static Capability<IElementInventory> ELEMENTINVENTORY_CAPABILITY;

	@Config(sync = true)
	public static int MAX_SIZE_ELEMENT_IN_INVENTORY = 10000;

	private ElementStack[] estacks;

	public ElementInventory() {
		this(1);
	}

	public ElementInventory(int slots) {
		setSlots(slots);
	}

	public ElementInventory(NBTTagCompound nbt) {
		estacks = new ElementStack[1];
		this.deserializeNBT(nbt);
	}

	public ElementInventory(ElementStack[] estacks) {
		this.estacks = estacks;
		if (this.estacks == null) this.estacks = new ElementStack[1];
	}

	public ElementStack[] getEStacksAndClear() {
		ElementStack[] origin = estacks;
		this.setSlots(origin.length);
		return origin;
	}

	@Override
	public void setSlots(int slots) {
		slots = slots <= 0 ? 1 : slots;
		slots = slots > 64 ? 64 : slots;
		estacks = new ElementStack[slots];
		for (int i = 0; i < estacks.length; i++) estacks[i] = ElementStack.EMPTY.copy();
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
		return MAX_SIZE_ELEMENT_IN_INVENTORY;
	}

	@Override
	public ElementStack setStackInSlot(int slot, ElementStack estack) {
		ElementStack old = estacks[slot];
		estacks[slot] = estack;
		return old;
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
		if (estack.isEmpty()) return true;
		ElementStack eorigin = getStackInSlot(slot);
		if (!eorigin.isEmpty() && !eorigin.areSameType(estack)) return false;
		int maxSize = getMaxSizeInSlot(slot);
		if (maxSize > 0) {
			if (eorigin.getCount() >= maxSize) return false;
			int newCount = eorigin.getCount() + estack.getCount();
			if (newCount >= MathHelper.ceil(maxSize * 1.025f)) return false;
		}
		if (simulate) return true;
		eorigin.growOrBecome(estack);
		return true;
	}

	@Override
	public ElementStack extractElement(int slot, @Nonnull ElementStack estack, boolean simulate) {
		if (estack.isEmpty()) return ElementStack.EMPTY.copy();
		ElementStack eorigin = getStackInSlot(slot);
		if (!eorigin.arePowerfulThan(estack)) return ElementStack.EMPTY.copy();
		int size = eorigin.getCount() >= estack.getCount() ? estack.getCount() : eorigin.getCount();
		ElementStack tmp = eorigin.copy();
		tmp.setCount(size);
		if (!simulate) eorigin.grow(-size);
		return tmp;
	}

	@Override
	public boolean hasState(NBTTagCompound nbt) {
		return nbt.hasKey("eInv", NBTTag.TAG_COMPOUND);
	}

	@Override
	public void loadState(NBTTagCompound nbt) {
		nbt = nbt.getCompoundTag("eInv");
		if (nbt != null) this.deserializeNBT(nbt);
	}

	@Override
	public void saveState(NBTTagCompound nbt) {
		NBTTagCompound dataNBT = this.serializeNBT();
		nbt.setTag("eInv", dataNBT);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) Provider.storage.writeNBT(ELEMENTINVENTORY_CAPABILITY, this, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		Provider.storage.readNBT(ELEMENTINVENTORY_CAPABILITY, this, null, nbt);
	}

	// ICanArrayDetected

	@Override
	public int getSize() {
		return getSlots();
	}

	@Override
	public void setSize(int size) {
		setSlots(size);
	}

	@Override
	public boolean hasChange(int index, ElementStack oldValue) {
		ElementStack eStack = this.getStackInSlot(index);
		return !eStack.areSameEqual(oldValue);
	}

	@Override
	public ElementStack copyCurrValue(int index) {
		return this.getStackInSlot(index).copy();
	}

	@Override
	public NBTTagIntArray serializeCurrValueToSend(int index) {
		ElementStack eStack = this.getStackInSlot(index);
		return new NBTTagIntArray(
				new int[] { Element.getIdFromElement(eStack.getElement()), eStack.getCount(), eStack.getPower() });
	}

	@Override
	public void deserializeCurrValueFromSend(int index, NBTTagIntArray nbtData) {
		int[] datas = nbtData.getIntArray();
		this.setStackInSlot(index, new ElementStack(Element.getElementFromId(datas[0]), datas[1], datas[2]));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ElementHelper.addElementInformation(this, worldIn, tooltip, flagIn);
	}

	// Stroage

	// 能力保存
	public static class Storage implements Capability.IStorage<IElementInventory> {

		@Override
		public NBTBase writeNBT(Capability<IElementInventory> capability, IElementInventory instance, EnumFacing side) {
			NBTTagCompound nbt = new NBTTagCompound();
			instance.writeCustomDataToNBT(nbt);
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < instance.getSlots(); i++) {
				if (instance.getStackInSlot(i).isEmpty()) continue;
				NBTTagCompound data = instance.getStackInSlot(i).serializeNBT();
				data.setInteger("slot", i);
				list.appendTag(data);
			}
			nbt.setTag("list", list);
			nbt.setInteger("size", instance.getSlots());
			return nbt;
		}

		@Override
		public void readNBT(Capability<IElementInventory> capability, IElementInventory instance, EnumFacing side,
				NBTBase tag) {
			if (tag == null) return;
			NBTTagCompound nbt = (NBTTagCompound) tag;
			int size = nbt.getInteger("size");
			if (instance instanceof IElementInventoryModifiable)
				((IElementInventoryModifiable) instance).setSlots(size);
			NBTTagList list = nbt.getTagList("list", 10);
			for (NBTBase base : list) {
				NBTTagCompound data = (NBTTagCompound) base;
				ElementStack etack = new ElementStack(data);
				int slot = data.getInteger("slot");
				if (slot < instance.getSlots())
					instance.setStackInSlot(slot, etack.isEmpty() ? ElementStack.EMPTY : etack);
			}
			instance.readCustomDataFromNBT(nbt);
		}

	}

	// 能力提供者
	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		private IElementInventory inventory;
		public final static IStorage<IElementInventory> storage = ELEMENTINVENTORY_CAPABILITY.getStorage();

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
			if (ELEMENTINVENTORY_CAPABILITY.equals(capability)) return (T) inventory;
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
