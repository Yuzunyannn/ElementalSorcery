package yuzunyannn.elementalsorcery.tile;

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

public class TileIceRockStand extends TileIceRockSendRecv implements ITickable {

	protected int tick;

	protected int linkCount = 0;
	/** 服务端检查变化量是否值得更新 */
	public double lastUpdateMagicFragment = 0;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("linkCount", linkCount);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		linkCount = compound.getInteger("linkCount");
		super.readFromNBT(compound);
	}

	/** 檢查合并建築 */
	public void checkAndBuildStructure() {

		linkCount = 0;
		for (int i = 0; i < BlockIceRockStand.TOWER_MAX_HEIGHT; i++) {
			BlockPos at = pos.up(i + 1);
			TileIceRockCrystalBlock tile = BlockHelper.getTileEntity(world, at, TileIceRockCrystalBlock.class);
			if (tile == null) break;
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
				tile.setMagicFragment(singleMagicFragment);
				tile.unlink();
			}
		}

		linkCount = 0;
		setMagicFragment(0);
		markDirty();
	}

	public void updateStandDataToClent() {
		lastUpdateMagicFragment = getMagicFragment();
		this.updateToClient();
	}

	public int getLinkCount() {
		return linkCount;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, linkCount + 1, 1));
	}

	@Override
	public double getMagicFragmentCapacity() {
		return linkCount == 0 ? 0 : Math.pow(10, linkCount + 1 + 6);
	}

	@Override
	public TileIceRockSendRecv getIceRockCore() {
		return this;
	}

	@Override
	public void update() {
		tick++;
		if (world.isRemote) return;

		// 大于0.5%才更新，不然没必要更新
		if (Math.abs(getMagicFragment() - lastUpdateMagicFragment) / getMagicFragmentCapacity() >= 0.005)
			updateStandDataToClent();
	}

}
