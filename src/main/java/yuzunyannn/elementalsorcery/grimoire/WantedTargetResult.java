package yuzunyannn.elementalsorcery.grimoire;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WantedTargetResult {

	public static final WantedTargetResult EMPTY = new WantedTargetResult();

	protected Entity entity;
	protected BlockPos pos;
	protected Vec3d hit;
	protected EnumFacing face;

	protected WantedTargetResult() {

	}

	public WantedTargetResult(Entity entity, Vec3d hit) {
		this.entity = entity;
		this.hit = hit;
	}

	public WantedTargetResult(BlockPos pos, EnumFacing face, Vec3d hit) {
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

}
