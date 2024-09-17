package yuzunyannn.elementalsorcery.util.element;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IDataSensitivity;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.computer.soft.CapabilityGetter;

public class ElementInventoryRemoteExchange implements IElementInventory, ICastable {

	protected CapabilityGetter<IElementInventory> remote = CapabilityGetter.emtpy();
	protected ElementInventoryCache cache;

	protected class ElementInventoryCache extends ElementInventory {

		protected int queueIndex;
		protected int transfer = -1;

		public ElementInventoryCache() {
			this(1);
		}

		public ElementInventoryCache(int slots) {
			super(slots);
		}

		@Override
		public int getMaxSizeInSlot(int slot) {
			return super.getMaxSizeInSlot(slot) * 2;
		}

		@Override
		public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
			if (estack.isEmpty()) return true;
			ElementStack eorigin = getStackInSlot(slot);
			if (!eorigin.isEmpty() && !eorigin.areSameType(estack)) return false;
			// must power is same if has remote
			if (remote.softGet() != null) {
				if (eorigin.getPower() != estack.getPower()) return false;
			}
			int maxSize = getMaxSizeInSlot(slot);
			if (maxSize > 0 && eorigin.getCount() >= maxSize) {
				transfer = slot;
				return false;
			}
			if (simulate) return true;
			eorigin.growOrBecome(estack);
			return true;
		}

		@Override
		public boolean insertElement(ElementStack estack, boolean simulate) {
			transfer = -1;
			int slots = this.getSlots();
			for (int i = 0; i < slots; i++) {
				if (insertElement((i + queueIndex) % slots, estack, simulate)) return true;
				if (transfer != -1) return false;
			}
			return false;
		}

		public boolean insertTransfer(IElementInventory remote, ElementStack estack, boolean simulate) {
			int slot;
			if (transfer == -1) slot = queueIndex;
			else slot = transfer;

			if (!remote.insertElement(getStackInSlot(slot), simulate)) return false;
			if (simulate) return true;

			setStackInSlot(slot, estack);
			if (transfer == -1) queueIndex = (queueIndex + 1) % this.getSlots();

			return true;
		}

		public ElementStack extractTransfer(IElementInventory remote, ElementStack extract, ElementStack estack,
				boolean simulate) {
			int slot = -1;

			int slots = this.getSlots();
			for (int i = 0; i < slots; i++) {
				int index = (slots - i + queueIndex - 1) % slots;
				ElementStack inner = getStackInSlot(index);
				if (inner.isEmpty()) {
					slot = index;
					break;
				}
				if (remote.insertElement(inner, simulate)) {
					if (!simulate) setStackInSlot(index, ElementStack.EMPTY);
					slot = index;
					break;
				}
			}

			if (slot == -1) return extract;

			int dCount = estack.getCount() - extract.getCount();
			int max = Math.max(dCount, getMaxSizeInSlot(slots) / 4);

			ElementStack cacheStack = estack.copy();
			cacheStack.setCount(max);

			ElementStack rStack = remote.extractElement(cacheStack, simulate);
			if (rStack.isEmpty()) return extract;

			dCount = Math.min(dCount, rStack.getCount());
			extract.grow(rStack.splitStack(dCount));
			setStackInSlot(slot, rStack);

			return extract;
		}

		public boolean transferBack(IElementInventory remote) {
			boolean hasBack = false;
			for (int i = 0; i < this.getSlots(); i++) {
				ElementStack inner = getStackInSlot(i);
				if (inner.isEmpty()) continue;
				if (remote.insertElement(inner, false)) {
					setStackInSlot(i, ElementStack.EMPTY);
					hasBack = true;
				}
			}
			return hasBack;
		}
	}

	public ElementInventoryRemoteExchange() {
		this(1);
	}

	public ElementInventoryRemoteExchange(int slots) {
		cache = new ElementInventoryCache(slots);
	}

	public void setRemote(CapabilityGetter<IElementInventory> remote) {
		this.remote = remote;
	}

	public IElementInventory getRemote() {
		return this.remote.softGet();
	}

	public ElementInventoryCache getCache() {
		return cache;
	}

	@Override
	public long contentHashCode() {
		return cache.contentHashCode();
	}

	@Override
	public void markDirty() {
		cache.markDirty();
	}

	@Override
	public void applyUse() {
		cache.applyUse();
	}

	@Override
	public IElementInventory assign(IElementInventory other) {
		cache.assign(other);
		return this;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return cache.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		cache.deserializeNBT(nbt);
	}

	public PacketBuffer serializeBuff(PacketBuffer buffer) {
		return cache.serializeBuff(buffer);
	}

	public void deserializeBuff(PacketBuffer buffer) {
		cache.deserializeBuff(buffer);
	}

	@Override
	public boolean hasState(NBTTagCompound nbt) {
		return cache.hasState(nbt);
	}

	@Override
	public void loadState(NBTTagCompound nbt) {
		cache.loadState(nbt);
	}

	@Override
	public void saveState(NBTTagCompound nbt) {
		cache.saveState(nbt);
	}

	@Override
	public int getSlots() {
		return cache.getSlots();
	}

	@Override
	public ElementStack getStackInSlot(int slot) {
		return cache.getStackInSlot(slot);
	}

	@Override
	public ElementStack setStackInSlot(int slot, ElementStack estack) {
		return cache.setStackInSlot(slot, estack);
	}

	@Override
	public boolean isEmpty() {
		return cache.isEmpty();
	}

	public void clear() {
		for (int i = 0; i < cache.getSlots(); i++) {
			cache.setStackInSlot(i, ElementStack.EMPTY);
		}
	}

	public boolean transferBack() {
		IElementInventory remote = this.remote.toughGet();
		if (remote == null) return false;
		return cache.transferBack(remote);
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {

		if (!simulate) onUpdateCall();

		if (this.cache.insertElement(estack, simulate)) {
			onElementBehavier(estack);
			return true;
		}

		IElementInventory remote = this.remote.toughGet();
		if (remote == null) return false;

		boolean success = this.cache.insertTransfer(remote, estack, simulate);
		if (success) onElementBehavier(estack);
		return success;
	}

	@Override
	public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {

		if (!simulate) onUpdateCall();

		ElementStack extract = this.cache.extractElement(estack, simulate);
		if (!extract.isEmpty()) onElementBehavier(estack);

		if (extract.getCount() >= estack.getCount()) return extract;

		IElementInventory remote = this.remote.toughGet();
		if (remote == null) return extract;

		return this.cache.extractTransfer(remote, extract, estack, simulate);
	}

	@Override
	public boolean insertElement(ElementStack estack, boolean simulate) {
		return insertElement(0, estack, simulate);
	}

	@Override
	public ElementStack extractElement(ElementStack estack, boolean simulate) {
		return extractElement(0, estack, simulate);
	}

	@Override
	public IElementInventory setSensor(IDataSensitivity sensor) {
		cache.setSensor(sensor);
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		cache.addInformation(worldIn, tooltip, flagIn);
	}

	@Override
	public <T> T cast(Class<?> to) {
		return cache.cast(to);
	}

	protected void onUpdateCall() {
	}

	protected void onElementBehavier(ElementStack eStack) {

	}

}
