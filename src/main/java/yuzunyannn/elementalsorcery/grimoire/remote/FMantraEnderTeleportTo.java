package yuzunyannn.elementalsorcery.grimoire.remote;

import java.util.List;
import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.WorldLocation;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class FMantraEnderTeleportTo extends FMantraBase {

	public FMantraEnderTeleportTo() {
		addCanUseElementWithSameLevel(ESObjects.ELEMENTS.ENDER);
		setMaxCharge(10000);
		setChargeSpeedRatio(10f / 10000f);
		setMinChargeRatio(1);
		setIconRes(initTexturePath());
	}

	String initTexturePath() {
		return "textures/mantras/teleport_f_goto.png";
	}

	@Override
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content) {
		double charge = content.get(CHARGE);
		;
		float range = (float) ((charge / 10000) * 4 + 4);
		ergodicTeleportEntities(world, pos, to, range, e -> {
			executeTeleport(world, pos, to, e);
			return null;
		});
		// sound
		World toWorld = to.getWorld(world);
		if (toWorld != null) {
			Vec3d toVec = new Vec3d(to.getPos()).add(0.5, 0.5, 0.5);
			toWorld.playSound(null, toVec.x, toVec.y, toVec.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
					SoundCategory.HOSTILE, 2, 1);
		}
		Vec3d fromVec = new Vec3d(pos).add(0.5, 0.5, 0.5);
		world.playSound(null, fromVec.x, fromVec.y, fromVec.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
				SoundCategory.HOSTILE, 2, 1);
	}

	protected void ergodicTeleportEntities(World world, BlockPos pos, WorldLocation to, float range,
			Function<Entity, Void> callback) {
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, range, range);
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, aabb, e -> {
			return e instanceof EntityLivingBase || e instanceof EntityItem;
		});
		for (Entity e : list) callback.apply(e);
	}

	protected void executeTeleport(World world, BlockPos pos, WorldLocation to, Entity target) {
		Vec3d at = target.getPositionVector().subtract(new Vec3d(pos)).add(new Vec3d(to.getPos()));
		at = findAccpetPlace(world, at);
		EntityPortal.moveTo(target, at, to.getDimension());
	}

	static public Vec3d findAccpetPlace(World world, Vec3d at) {
		BlockPos atPos = new BlockPos(at);
		for (int i = 0; i < 10; i++) {
			int y = i / 2 + 1;
			if (i % 2 == 0) y = -y;
			BlockPos checkPos = atPos.up(y);
			if (BlockHelper.isPassableBlock(world, checkPos) && BlockHelper.isPassableBlock(world, checkPos.up())) {
				at = at.add(0, y, 0);
				break;
			}
		}
		return at;
	}

}
