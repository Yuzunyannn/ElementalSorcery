package yuzunyannn.elementalsorcery.tile.md;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import yuzunyannn.elementalsorcery.api.tile.IAcceptMagic;
import yuzunyannn.elementalsorcery.api.tile.IAcceptMagicPesky;

public class TileMDTransfer extends TileMDBase implements ITickable {

	@Override
	protected int getOverflow() {
		return 0;
	}

	@Override
	public boolean canRecvMagic(EnumFacing facing) {
		return true;
	}

	@Override
	protected boolean canSend(EnumFacing facing) {
		if (facing == EnumFacing.UP)
			return this.world.getTileEntity(this.pos.offset(EnumFacing.UP)) instanceof IAcceptMagic
					|| this.hasTorch(facing);
		else
			return facing.getHorizontalIndex() >= 0 && this.hasTorch(facing);
	}

	@Override
	protected void fixedDyLev() {
		TargetInfo info = targets[EnumFacing.UP.getIndex()];
		if (info != null) {
			IAcceptMagicPesky accept = info.to(IAcceptMagicPesky.class);
			if (accept != null)
				info.dyLev = accept.getCurrentCapacity() > this.getCurrentCapacity() ? -1 : 0;
		}
	}

	@Override
	public void update() {
		this.autoTransfer();
	}
}
