package yuzunyannn.elementalsorcery.api.util.target;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.util.IAliveStatusable;

public class CORTile extends CapabilityObjectRef {

	public static class Storage implements ICapabilityRefStorage<CORTile> {
		@Override
		public void write(ByteBuf buf, CORTile obj) {
			buf.writeInt(obj.pos.getX());
			buf.writeInt(obj.pos.getY());
			buf.writeInt(obj.pos.getZ());
		}

		@Override
		public CORTile read(ByteBuf buf) {
			int x = buf.readInt();
			int y = buf.readInt();
			int z = buf.readInt();
			return new CORTile(new BlockPos(x, y, z));
		}
	}

	protected BlockPos pos;
	protected TileEntity ref;
	protected IAliveStatusable status;

	public CORTile(TileEntity tile) {
		pos = tile.getPos();
		worldId = tile.getWorld().provider.getDimension();
		ref = tile;
		if (ref instanceof IAliveStatusable) status = (IAliveStatusable) ref;
	}

	protected CORTile(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean equals(CapabilityObjectRef other) {
		return pos.equals(((CORTile) other).pos);
	}

	@Override
	public void restore(World world) {
		ref = null;
		if (world.provider.getDimension() != worldId) {
			if (world instanceof WorldServer) {
				world = DimensionManager.getWorld(worldId);
				if (world == null) return;
			}
		}
		if (!world.isBlockLoaded(pos)) return;
		ref = world.getTileEntity(pos);
		if (ref == null) return;
		if (ref instanceof IAliveStatusable) status = (IAliveStatusable) ref;
		else status = null;
	}

	@Override
	public boolean checkReference() {
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
	public IWorldObject toWorldObject() {
		return ref == null ? null : new WorldObjectBlock(ref);
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

	@Override
	public Object toDisplayObject() {
		List<String> list = new LinkedList<>();
		list.add(String.format("World: %d", worldId));
		list.add("Location: " + String.format("(%d,%d,%d)", pos.getX(), pos.getY(), pos.getZ()));
		return list;
	}

}
