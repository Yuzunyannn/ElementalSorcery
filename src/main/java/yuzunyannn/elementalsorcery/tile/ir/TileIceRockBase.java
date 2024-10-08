package yuzunyannn.elementalsorcery.tile.ir;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IElementInventoryPromote;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.tile.TileEntityNetworkOld;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryAdapter;

public abstract class TileIceRockBase extends TileEntityNetworkOld implements IElementInventoryPromote {

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
	}

	/**
	 * @retrun 剩余没有成功插入的片元
	 */
	abstract public double insertMagicFragment(double count, boolean simulate);

	/**
	 * @retrun 真正能取出来的量
	 */
	abstract public double extractMagicFragment(double count, boolean simulate);

	abstract public double getMagicFragment();

	abstract protected void setMagicFragment(double fragment);

	@Override
	public boolean canInventoryOperateBy(Object operater) {
		return true;
	}

	protected void checkElementInventoryStatusChange() {
	}

//	@Override
//	public void onInventoryStatusChange() {
//	}

	protected void onFromOrToFragmentChange(double wastageFragment) {
		markDirty();
	}

	protected class IceRockElementInventory extends ElementInventoryAdapter {

		@Override
		public int getSlots() {
			return 4;
		};

		@Override
		public int getMaxSizeInSlot(int slot) {
			return -1;
		}

		public int getPowerFromSlot(int slot) {
			return (int) (50 * Math.pow(2, (slot)));
		}

		@Override
		public ElementStack getStackInSlot(int slot) {
			int power = getPowerFromSlot(slot);
			if (power <= 0) return ElementStack.EMPTY;
			int count = MathHelper.floor(ElementTransition.fromFragmentByPower(ESObjects.ELEMENTS.MAGIC, getMagicFragment(), power));
			if (count <= 0) return ElementStack.EMPTY;
			return ElementStack.magic(count, power);
		};

		@Override
		public ElementStack setStackInSlot(int slot, ElementStack estack) {
			ElementStack origin = getStackInSlot(slot);
			setMagicFragment(ElementTransition.toFragment(estack.toMagic(world)));
			return origin;
		};

		@Override
		public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
			if (estack.isEmpty()) return true;
			if (!estack.isMagic()) estack = estack.toMagic(world);
			double fragment = ElementTransition.toFragment(estack);
			double rest = insertMagicFragment(fragment, simulate);
			if (rest == fragment) return false;
			if (!simulate) onFromOrToFragmentChange(rest);
			return true;
		};

		@Override
		public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {
			if (!estack.isMagic()) return ElementStack.EMPTY;
			int power = estack.getPower();
			double fragment = extractMagicFragment(ElementTransition.toFragment(estack), simulate);
			double dcount = ElementTransition.fromFragmentByPower(ESObjects.ELEMENTS.MAGIC, fragment, power);
			int count = MathHelper.floor(dcount);
			if (!simulate)
				onFromOrToFragmentChange(ElementTransition.toFragment(ESObjects.ELEMENTS.MAGIC, dcount - count, power));
			return ElementStack.magic(count, power);
		};

		@Override
		public long contentHashCode() {
			return getMagicFragment() == 0 ? 0 : 1;
		}

		@Override
		public void markDirty() {
			checkElementInventoryStatusChange();
			this.markDirty();
		}
	}

	protected IceRockElementInventory eInventoryAdapter = new IceRockElementInventory();

	public IElementInventory getElementInventoryAdapter() {
		return eInventoryAdapter;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return (T) eInventoryAdapter;
		return super.getCapability(capability, facing);
	}

}
