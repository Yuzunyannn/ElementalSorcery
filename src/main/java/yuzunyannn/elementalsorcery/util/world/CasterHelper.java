package yuzunyannn.elementalsorcery.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.grimoire.WantedTargetResult;

public class CasterHelper {

	public static boolean canStand(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return block.isReplaceable(world, pos);
	}

	public static BlockPos findFoothold(EntityLivingBase entity, float dis) {
		World world = entity.world;
		RayTraceResult rt = WorldHelper.getLookAtBlock(world, entity, dis);
		if (rt == null) return null;
		BlockPos pos = new BlockPos(rt.hitVec);
		if (canStand(world, pos)) pos = pos.offset(rt.sideHit, -1);
		if (canStand(world, pos.up()) && canStand(world, pos.up(2))) return pos.up();
		pos = pos.offset(rt.sideHit, 1);
		if (canStand(world, pos) && canStand(world, pos.up())) return pos;
		return null;
	}

	public static WantedTargetResult findLookBlockResult(EntityLivingBase entity, float dis) {
		RayTraceResult rt = WorldHelper.getLookAtBlock(entity.world, entity, dis);
		if (rt == null) return WantedTargetResult.EMPTY;
		if (rt.getBlockPos() == null) return WantedTargetResult.EMPTY;
		return new WantedTargetResult(rt.getBlockPos(), rt.sideHit, rt.hitVec);
	}

	public static <T extends Entity> WantedTargetResult findLookTargetResult(Class<T> cls, EntityLivingBase entity,
			float dis) {
		RayTraceResult rt = WorldHelper.getLookAtEntity(entity.world, entity, 64, cls);
		if (rt == null) return WantedTargetResult.EMPTY;
		return new WantedTargetResult(rt.entityHit, rt.hitVec);
	}

}