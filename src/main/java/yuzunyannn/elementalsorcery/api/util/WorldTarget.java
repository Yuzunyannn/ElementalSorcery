package yuzunyannn.elementalsorcery.api.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WorldTarget {

	public static final WorldTarget EMPTY = new WorldTarget();

	protected Entity entity;
	protected BlockPos pos;
	protected Vec3d hit;
	protected EnumFacing face;

	protected WorldTarget() {

	}

	public WorldTarget(Entity entity, Vec3d hit) {
		this.entity = entity;
		this.hit = hit;
	}

	public WorldTarget(BlockPos pos, EnumFacing face, Vec3d hit) {
		this.pos = pos;
		this.face = face;
		this.hit = hit;
	}

	@Nonnull
	public EnumFacing getFace() {
		return face == null ? EnumFacing.UP : face;
	}

	@Nonnull
	public Vec3d getHitVec() {
		return hit == null ? Vec3d.ZERO : hit;
	}

	@Nullable
	public BlockPos getPos() {
		return pos;
	}

	@Nullable
	public Entity getEntity() {
		return entity;
	}

	public boolean isEmpty() {
		return entity == null && pos == null;
	}

	@Nullable
	public IWorldObject toWorldObject(World world) {
		if (entity != null) return new WorldObjectEntity(entity);
		if (pos != null) return new WorldObjectBlock(world, pos);
		return null;
	}
}
