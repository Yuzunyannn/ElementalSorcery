package yuzunyannn.elementalsorcery.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

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

}
