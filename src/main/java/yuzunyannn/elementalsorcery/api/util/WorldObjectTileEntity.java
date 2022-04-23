package yuzunyannn.elementalsorcery.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class WorldObjectTileEntity implements IWorldObject {

	public final TileEntity tile;

	public WorldObjectTileEntity(TileEntity tile) {
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
		return new Vec3d(tile.getPos()).add(0.5, 0, 0.5);
	}

	@Override
	public BlockPos getPosition() {
		return tile.getPos();
	}

	@Override
	public World getWorld() {
		return tile.getWorld();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return tile.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return tile.getCapability(capability, facing);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof WorldObjectTileEntity) return ((WorldObjectTileEntity) obj).tile == this.tile;
		return false;
	}
}
