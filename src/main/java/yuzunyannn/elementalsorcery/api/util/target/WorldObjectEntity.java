package yuzunyannn.elementalsorcery.api.util.target;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class WorldObjectEntity implements IWorldObject {

	public final Entity entity;

	public WorldObjectEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public Vec3d getObjectPosition() {
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof WorldObjectEntity) return ((WorldObjectEntity) obj).entity == this.entity;
		return false;
	}
}
