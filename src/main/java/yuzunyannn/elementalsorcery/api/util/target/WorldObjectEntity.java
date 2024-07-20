package yuzunyannn.elementalsorcery.api.util.target;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.util.GameCast;
import yuzunyannn.elementalsorcery.api.util.ICastEnv;

public class WorldObjectEntity implements IWorldObject {

	public final Entity entity;

	public WorldObjectEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public <T> T to(Class<T> cls) {
		if (cls == CapabilityObjectRef.class) return (T) toRef();
		if (cls == BlockPos.class) return (T) getPosition();
		return GameCast.cast(ICastEnv.EMPTY, entity, cls);
	}

	@Override
	public CapabilityObjectRef toRef() {
		return CapabilityObjectRef.of(entity);
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
	public TileEntity toTileEntity() {
		return null;
	}

	@Override
	public Entity toEntity() {
		return entity;
	}

	@Override
	public World getWorld() {
		return entity.world;
	}

	@Override
	public boolean isAlive() {
		return !entity.isDead;
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
