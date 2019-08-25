package yuzunyannn.elementalsorcery.tile.md;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.tile.TileHearth;

public class TileMDHearth extends TileMDBase implements ITickable {

	public boolean isFire() {
		return world.getBlockState(pos).getValue(BlockHearth.BURNING);
	}

	// 剩余燃烧时间
	protected int burnTime = 0;

	private void brunIt(boolean burn) {
		IBlockState state = world.getBlockState(pos);
		if (burn != state.getValue(BlockHearth.BURNING))
			world.setBlockState(pos, state.withProperty(BlockHearth.BURNING, burn));
	}

	@Override
	public void update() {
		this.autoTransfer();
		TileEntity tile = world.getTileEntity(pos.up());
		if (world.isRemote) {
			if (this.isFire())
				TileHearth.giveBurnPower(tile, 1, 4);
			return;
		}
		if (burnTime == 0)
			this.gainPower(tile, true);
		boolean need = false;
		if (burnTime > 0) {
			burnTime--;
			need = TileHearth.giveBurnPower(tile, 1, 4);
		}
		if (burnTime == 0 && need) {
			this.gainPower(tile, false);
			this.markDirty();
			if (burnTime == 0)
				brunIt(false);
		}
	}

	private void gainPower(TileEntity tile, boolean needTest) {
		if (burnTime == 0) {
			if (this.magic.isEmpty())
				return;
			// 测试楼上是否需要
			if (needTest && !TileHearth.giveBurnPower(tile, 0, 0))
				return;
			burnTime = 20 + (int) MathHelper.sqrt(this.magic.getPower());
			this.magic.shrink(1);
			brunIt(true);
		}
	}
}
