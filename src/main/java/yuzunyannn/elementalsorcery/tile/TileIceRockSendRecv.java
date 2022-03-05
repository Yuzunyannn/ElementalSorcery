package yuzunyannn.elementalsorcery.tile;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.element.ElementStack;

abstract class TileIceRockSendRecv extends TileEntityNetwork {

	static final double ln2 = Math.log(2);

	static public double toElementFragment(ElementStack eStack) {
		return Math.pow(2.4, Math.log(eStack.getPower()) / ln2) * eStack.getCount();
	}

	private double magicFragment = 0;

	public double getMagicFragment() {
		return magicFragment;
	}

	protected void setMagicFragment(double magicFragment) {
		this.magicFragment = magicFragment;
	}

	protected double getMagicFragmentCapacity() {
		return 0;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble("fragment", getMagicFragment());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		setMagicFragment(compound.getDouble("fragment"));
		super.readFromNBT(compound);
	}

	/** 获取核心能量单位 */
	@Nullable
	public abstract TileIceRockSendRecv getIceRockCore();

	public double insertMagicFragment(double count, boolean simulate) {
		TileIceRockSendRecv core = getIceRockCore();
		if (core == null) return count;
		return core.insertMagicFragmentCore(count, simulate);
	}

	public double extractMagicFragment(double count, boolean simulate) {
		TileIceRockSendRecv core = getIceRockCore();
		if (core == null) return 0;
		return core.extractMagicFragmentCore(count, simulate);
	}

	protected double insertMagicFragmentCore(double count, boolean simulate) {
		double newCount = getMagicFragment() + count;
		double capacity = getMagicFragmentCapacity();
		double remian = 0;
		if (newCount > capacity) {
			remian = newCount - capacity;
			newCount = capacity;
		}
		if (simulate) return remian;
		setMagicFragment(newCount);
		markDirty();
		return remian;
	}

	protected double extractMagicFragmentCore(double count, boolean simulate) {
		double fragment = getMagicFragment();
		double extract = Math.min(fragment, count);
		if (simulate) return extract;
		setMagicFragment(fragment - extract);
		markDirty();
		return extract;
	}

}
