package yuzunyannn.elementalsorcery.api.util.target;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.tile.IAliveStatusable;

public class CORTile extends CapabilityObjectRef {

	protected WorldLocation local;

	protected TileEntity ref;
	protected IAliveStatusable status;

	public CORTile(TileEntity tile) {
		local = new WorldLocation(tile);
		ref = tile;
		if (ref instanceof IAliveStatusable) status = (IAliveStatusable) ref;
	}

	@Override
	public void restore(World world) {
		ref = null;
		if (world.provider.getDimension() != local.dimId) {
			if (world instanceof WorldServer) {
				world = DimensionManager.getWorld(local.dimId);
				if (world == null) return;
			}
		}
		if (!world.isBlockLoaded(local.pos)) return;
		ref = world.getTileEntity(local.pos);
		if (ref == null) return;
		if (ref instanceof IAliveStatusable) status = (IAliveStatusable) ref;
		else status = null;
	}

	@Override
	public boolean isValid() {
		if (_isValid()) return true;
		ref = null;
		return false;
	}

	private boolean _isValid() {
		if (ref == null) return false;
		if (status != null) return status.isAlive();
		return !ref.isInvalid();
	}

	@Override
	public int tagId() {
		return TAG_TILE;
	}

	@Override
	public TileEntity toTileEntity() {
		return ref;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return ref == null ? false : ref.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return ref == null ? null : ref.getCapability(capability, facing);
	}

}
