package yuzunyannn.elementalsorcery.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CasterObjectTileEntity implements ICasterObject {

	public final TileEntity tile;

	public CasterObjectTileEntity(TileEntity tile) {
		this.tile = tile;
	}

	@Override
	public TileEntity asTileEntity() {
		return tile;
	}

	@Override
	public Entity asEntity() {
		return null;
	}

	@Override
	public Vec3d getPositionVector() {
		return new Vec3d(tile.getPos());
	}

	@Override
	public BlockPos getPosition() {
		return tile.getPos();
	}

	@Override
	public World getWorld() {
		return tile.getWorld();
	}

}
