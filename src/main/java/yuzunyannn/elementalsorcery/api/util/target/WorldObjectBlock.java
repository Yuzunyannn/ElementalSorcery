package yuzunyannn.elementalsorcery.api.util.target;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class WorldObjectBlock implements IWorldObject {

	private final BlockPos pos;
	private final World world;
	private final TileEntity tile;

	public WorldObjectBlock(World world, BlockPos pos) {
		this.pos = pos;
		this.world = world;
		this.tile = this.world.getTileEntity(pos);
	}

	public WorldObjectBlock(TileEntity tile) {
		this.pos = tile.getPos();
		this.world = tile.getWorld();
		this.tile = tile;
	}

	@Override
	public TileEntity toTileEntity() {
		return this.tile;
	}

	@Override
	public Entity toEntity() {
		return null;
	}

	@Override
	public Vec3d getObjectPosition() {
		return new Vec3d(getPosition()).add(0.5, 0, 0.5);
	}

	@Override
	public boolean isAlive() {
		if (tile == null) return true;
		return !tile.isInvalid();
	}

	@Override
	public BlockPos getPosition() {
		return tile != null ? tile.getPos() : pos;
	}

	@Override
	public World getWorld() {
		return tile != null ? tile.getWorld() : world;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (this.tile == null) return false;
		return tile.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (this.tile == null) return null;
		return tile.getCapability(capability, facing);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof WorldObjectBlock) {
			WorldObjectBlock other = (WorldObjectBlock) obj;
			return other.getPosition().equals(this.getPosition()) && other.getWorld() == this.getWorld();
		}
		return false;
	}
}
