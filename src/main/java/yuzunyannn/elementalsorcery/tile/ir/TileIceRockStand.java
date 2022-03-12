package yuzunyannn.elementalsorcery.tile.ir;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.block.container.BlockIceRockStand;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class TileIceRockStand extends TileIceRockBase implements ITickable {

	protected int tick;

	protected double magicFragment = 0;
	protected int linkCount = 0;
	/** 服务端检查变化量是否值得更新 */
	public double lastUpdateMagicFragment = 0;

	@Override
	public double getMagicFragment() {
		return magicFragment;
	}

	@Override
	protected void setMagicFragment(double magicFragment) {
		this.magicFragment = magicFragment;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("linkCount", linkCount);
		compound.setDouble("fragment", getMagicFragment());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		linkCount = compound.getInteger("linkCount");
		setMagicFragment(compound.getDouble("fragment"));
		super.readFromNBT(compound);
	}

	/** 檢查合并建築 */
	public void checkAndBuildStructure() {

		linkCount = 0;
		for (int i = 0; i < BlockIceRockStand.TOWER_MAX_HEIGHT; i++) {
			BlockPos at = pos.up(i + 1);
			TileIceRockCrystalBlock tile = BlockHelper.getTileEntity(world, at, TileIceRockCrystalBlock.class);
			if (tile == null) break;
			if (tile.canNotLinkMark) break;
			setMagicFragment(getMagicFragment() + tile.transferMagicFragment());
			tile.link(pos);
			linkCount++;
		}

		markDirty();
	}

	/** 檢查摧毀建築 */
	public void checkAndBreakStructure() {

		List<TileIceRockCrystalBlock> breakList = new ArrayList<>(this.linkCount);
		for (int i = 0; i < BlockIceRockStand.TOWER_MAX_HEIGHT; i++) {
			BlockPos at = pos.up(i + 1);
			TileIceRockCrystalBlock tile = BlockHelper.getTileEntity(world, at, TileIceRockCrystalBlock.class);
			if (tile == null) continue;
			if (pos.equals(tile.getLinkPos())) breakList.add(tile);
		}

		if (!breakList.isEmpty()) {
			double singleMagicFragment = getMagicFragment() / breakList.size();
			for (TileIceRockCrystalBlock tile : breakList) {
				tile.setMagicFragmentOwn(singleMagicFragment);
				tile.unlink();
			}
		}

		linkCount = 0;
		setMagicFragment(0);
		markDirty();
	}

	public void updateStandDataToClent() {
		System.out.print("??");
		lastUpdateMagicFragment = getMagicFragment();
		this.updateToClient();
	}

	@Override
	public void onInventoryStatusChange() {
		updateStandDataToClent();
	}

	public int getLinkCount() {
		return linkCount;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, linkCount + 1, 1));
	}

	public double getMagicFragmentCapacity() {
		return linkCount == 0 ? 0 : Math.pow(10, linkCount + 1 + 6);
	}

	public double getMaxFragmentOnceTransfer() {
		return getMagicFragmentCapacity() / 32;
	}

//	/** 高速缓存 */
//	protected WeakReference<TileIceRockSendRecv>[] tileCrystals;

	public TileIceRockSendRecv getTileCrystal(int n) {
		TileIceRockSendRecv tile = BlockHelper.getTileEntity(world, pos.up(n + 1), TileIceRockSendRecv.class);
		return tile;
	}

	@Override
	public void update() {
		tick++;

		for (int i = 0; i < linkCount; i++) {
			TileIceRockSendRecv tile = getTileCrystal(i);
			// 遍历中，出现找不到的情况，说明由于位置原因缺了一个
			if (tile == null) {
				this.checkAndBreakStructure();
				this.checkAndBuildStructure();
				this.updateStandDataToClent();
				return;
			}
			tile.onUpdate();
		}

		if (world.isRemote) return;

		if (tick % 20 == 0) {
			double fragment = getMagicFragment();
			double log10 = Math.max(Math.log10(fragment) - 3, 1);
			if (Math.ceil(fragment / log10) != Math.ceil(lastUpdateMagicFragment / log10)) updateStandDataToClent();
		}
	}

	/**
	 * 
	 * return 还剩多少没插入 The remaining Fragment that was not inserted
	 */
	@Override
	public double insertMagicFragment(double count, boolean simulate) {
		double fragment = getMagicFragment();
		double capacity = getMagicFragmentCapacity();
		if (fragment >= capacity) return count;
		double newCount = fragment + count;
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

	@Override
	public double extractMagicFragment(double count, boolean simulate) {
		double fragment = getMagicFragment();
		double extract = Math.min(fragment, count);
		if (simulate) return extract;
		setMagicFragment(fragment - extract);
		markDirty();
		return extract;
	}

}
