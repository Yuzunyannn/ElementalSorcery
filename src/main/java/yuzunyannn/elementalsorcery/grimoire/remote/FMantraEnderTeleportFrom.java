package yuzunyannn.elementalsorcery.grimoire.remote;

import java.util.List;
import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.WorldLocation;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class FMantraEnderTeleportFrom extends FMantraEnderTeleportTo {

	String initTexturePath() {
		return "textures/mantras/teleport_f_return.png";
	}

	@Override
	protected void ergodicTeleportEntities(World world, BlockPos pos, WorldLocation to, float range,
			Function<Entity, Void> callback) {
		World toWorld = to.getWorld(world);
		AxisAlignedBB aabb = WorldHelper.createAABB(to.getPos(), range, range, range);
		List<Entity> list = toWorld.getEntitiesWithinAABB(Entity.class, aabb, e -> {
			return e instanceof EntityLivingBase || e instanceof EntityItem;
		});
		for (Entity e : list) callback.apply(e);
	}

	@Override
	protected void executeTeleport(World world, BlockPos pos, WorldLocation to, Entity target) {
		Vec3d at = target.getPositionVector().subtract(new Vec3d(to.getPos())).add(new Vec3d(pos));
		at = findAccpetPlace(world, at);
		EntityPortal.moveTo(target, at, world.provider.getDimension());
	}
}
