package yuzunyannn.elementalsorcery.tile.ir;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IElementInventoryPromote;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryAdapter;

public abstract class TileIceRockBase extends TileEntityNetwork implements IElementInventoryPromote {

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

	@Override
	public void onInventoryStatusChange() {
	}

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
			int count = MathHelper.floor(ElementHelper.fromFragmentByPower(ESInit.ELEMENTS.MAGIC, getMagicFragment(), power));
			if (count <= 0) return ElementStack.EMPTY;
			return ElementStack.magic(count, power);
		};

		@Override
		public ElementStack setStackInSlot(int slot, ElementStack estack) {
			ElementStack origin = getStackInSlot(slot);
			setMagicFragment(ElementHelper.toFragment(estack.toMagic(world)));
			return origin;
		};

		@Override
		public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
			if (estack.isEmpty()) return true;
			if (!estack.isMagic()) estack = estack.toMagic(world);
			double fragment = ElementHelper.toFragment(estack);
			double rest = insertMagicFragment(fragment, simulate);
			if (rest == fragment) return false;
			if (!simulate) onFromOrToFragmentChange(rest);
			return true;
		};

		@Override
		public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {
			if (!estack.isMagic()) return ElementStack.EMPTY;
			int power = estack.getPower();
			double fragment = extractMagicFragment(ElementHelper.toFragment(estack), simulate);
			double dcount = ElementHelper.fromFragmentByPower(ESInit.ELEMENTS.MAGIC, fragment, power);
			int count = MathHelper.floor(dcount);
			if (!simulate)
				onFromOrToFragmentChange(ElementHelper.toFragment(ESInit.ELEMENTS.MAGIC, dcount - count, power));
			return ElementStack.magic(count, power);
		};

	}

	protected IceRockElementInventory eInventoryAdapter = new IceRockElementInventory();

	public IElementInventory getElementInventoryAdapter() {
		return eInventoryAdapter;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY.equals(capability)) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY.equals(capability)) return (T) eInventoryAdapter;
		return super.getCapability(capability, facing);
	}

}
