package yuzunyannn.elementalsorcery.grimoire;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class CasterObjectEntity implements ICasterObject {

	public final Entity entity;

	public CasterObjectEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public Vec3d getPositionVector() {
		return entity.getPositionVector();
	}

	@Override
	public BlockPos getPosition() {
		return entity.getPosition();
	}

	@Override
	public TileEntity asTileEntity() {
		return null;
	}

	@Override
	public Entity asEntity() {
		return entity;
	}

	@Override
	public World getWorld() {
		return entity.world;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return entity.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return entity.getCapability(capability, facing);
	}
}
