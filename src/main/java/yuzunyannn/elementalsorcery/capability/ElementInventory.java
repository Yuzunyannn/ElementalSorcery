package yuzunyannn.elementalsorcery.capability;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
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
import yuzunyannn.elementalsorcery.api.ESStorageKeyEnum;
import yuzunyannn.elementalsorcery.api.crafting.IDataSensitivity;
import yuzunyannn.elementalsorcery.api.crafting.IItemCapbiltitySyn;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IElementInventoryModifiable;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.ContainerArrayDetecter;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ElementInventory implements IElementInventoryModifiable, INBTSerializable<NBTTagCompound>,
		IItemCapbiltitySyn, ContainerArrayDetecter.ICanArrayDetected<ElementStack, NBTTagIntArray>, ICastable, INBTSS {

	@CapabilityInject(IElementInventory.class)
	public static Capability<IElementInventory> ELEMENTINVENTORY_CAPABILITY;

	@Config(sync = true)
	@Config.NumberRange(min = 1000, max = 0x00ffffff)
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
		estacks[slot] = ElementStack.EMPTY == estack ? estack.copy() : estack;
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
	public void writeSaveData(INBTWriter writer) {
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < getSlots(); i++) {
			if (getStackInSlot(i).isEmpty()) continue;
			NBTTagCompound data = getStackInSlot(i).serializeNBT();
			data.setInteger("slot", i);
			list.appendTag(data);
		}
		writer.write("list", list);
		writer.write("size", getSlots());
	}

	@Override
	public void readSaveData(INBTReader reader) {
		int size = reader.nint("size");
		this.setSlots(size);
		NBTTagList list = reader.listTag("list", NBTTag.TAG_COMPOUND);
		for (NBTBase base : list) {
			NBTTagCompound data = (NBTTagCompound) base;
			ElementStack etack = new ElementStack(data);
			int slot = data.getInteger("slot");
			if (slot < getSlots()) setStackInSlot(slot, etack.isEmpty() ? ElementStack.EMPTY : etack);
		}
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writer.writeStream("n", buff -> serializeBuff(buff));
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		reader.sobj("n", buff -> {
			deserializeBuff(buff);
			return this;
		});
	}

	public PacketBuffer serializeBuff(PacketBuffer buffer) {
		buffer.writeInt(estacks.length);
		for (int i = 0; i < estacks.length; i++) estacks[i].serializeBuff(buffer);
		return buffer;
	}

	public void deserializeBuff(PacketBuffer buffer) {
		int size = buffer.readInt();
		this.setSlots(size);
		for (int i = 0; i < size; i++) estacks[i] = new ElementStack(buffer);
	}

	public static boolean hasInvData(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return false;
		return nbt.hasKey(ESStorageKeyEnum.ELEMENT_INV, NBTTag.TAG_COMPOUND);
	}

	// IDataSensitivity
	private IDataSensitivity sensor;

	@Override
	public void markDirty() {
		if (sensor != null) sensor.markDirty();
	}

	@Override
	public void applyUse() {
		if (sensor != null) sensor.applyUse();
	}

	@Override
	public ElementInventory setSensor(IDataSensitivity sensor) {
		this.sensor = sensor;
		return this;
	}

	public static class ItemStackDataSensitivity implements IDataSensitivity, ICastable {
		final ItemStack stack;
		final IElementInventory self;

		public ItemStackDataSensitivity(IElementInventory self, ItemStack stack) {
			this.stack = stack;
			this.self = self;
		}

		@Override
		public void markDirty() {
			NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
			NBTTagCompound dat = (NBTTagCompound) Provider.storage.writeNBT(ELEMENTINVENTORY_CAPABILITY, self, null);
			nbt.setTag(ESStorageKeyEnum.ELEMENT_INV, dat);
		}

		@Override
		public void applyUse() {
			NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
			nbt = nbt.getCompoundTag(ESStorageKeyEnum.ELEMENT_INV);
			if (nbt != null) Provider.storage.readNBT(ELEMENTINVENTORY_CAPABILITY, self, null, nbt);
		}

		@Override
		public <T> T cast(Class<?> to) {
			if (to == ItemStack.class) return (T) stack;
			return null;
		}
	}

	public static <T extends IElementInventory> T sensor(T self, ItemStack stack) {
		self.setSensor(new ItemStackDataSensitivity(self, stack));
		return self;
	}

	public static <T extends IElementInventory> T sensor(T self, IDataSensitivity sensor) {
		self.setSensor(sensor);
		return self;
	}

	// IAssignable

	@Override
	public ElementInventory assign(IElementInventory other) {
		if (this == other) return this;
		setSlots(other.getSlots());
		for (int i = 0; i < Math.min(this.getSlots(), other.getSlots()); i++)
			this.setStackInSlot(i, other.getStackInSlot(i).copy());
		for (int i = other.getSlots(); i < this.getSlots(); i++) this.setStackInSlot(i, ElementStack.EMPTY);
		return this;
	}

	// ICastable

	@Override
	public <T> T cast(Class<?> to) {
		return GameCast.cast(sensor, to);
	}

	// IItemCapbiltitySyn

	@Override
	@Deprecated
	public boolean hasState(NBTTagCompound nbt) {
		return nbt.hasKey(ESStorageKeyEnum.ELEMENT_INV, NBTTag.TAG_COMPOUND);
	}

	@Override
	@Deprecated
	public void loadState(NBTTagCompound nbt) {
		nbt = nbt.getCompoundTag(ESStorageKeyEnum.ELEMENT_INV);
		if (nbt != null) this.deserializeNBT(nbt);
	}

	@Override
	@Deprecated
	public void saveState(NBTTagCompound nbt) {
		NBTTagCompound dataNBT = this.serializeNBT();
		nbt.setTag(ESStorageKeyEnum.ELEMENT_INV, dataNBT);
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
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<IElementInventory> capability, IElementInventory instance, EnumFacing side,
				NBTBase tag) {
			if (tag == null) return;
			instance.deserializeNBT((NBTTagCompound) tag);
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
