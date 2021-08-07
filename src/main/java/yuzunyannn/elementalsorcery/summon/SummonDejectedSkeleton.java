package yuzunyannn.elementalsorcery.summon;

import java.util.List;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.mob.EntityDejectedSkeleton;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class SummonDejectedSkeleton extends SummonCommon {

	public SummonDejectedSkeleton(World world, BlockPos pos) {
		super(world, pos, 0x757575);
	}

	@Override
	public void initData() {
		this.size = 16;
		this.height = 8;
	}

	@Override
	public boolean update() {
		if (tick++ % 40 != 0) return true;
		if (world.isRemote) return true;

		AxisAlignedBB aabb = WorldHelper.createAABB(pos, 8, 4, 2);
		List<EntitySkeleton> entities = world.getEntitiesWithinAABB(EntitySkeleton.class, aabb);
		if (entities.isEmpty()) return true;

		int times = 1;
		int i = 0;

		if (world.rand.nextInt(5) == 0) times++;
		while (!entities.isEmpty()) {
			EntitySkeleton selected = entities.get(world.rand.nextInt(entities.size()));
			entities.remove(selected);
			if (selected.getClass() != EntitySkeleton.class) continue;

			EntityDejectedSkeleton dejectedSkeleton = new EntityDejectedSkeleton(selected);
			world.spawnEntity(dejectedSkeleton);
			selected.setDead();

			Effects.spawnSummonEntity(dejectedSkeleton, new int[] { 0x151515, 0x5c5c5c, 0xb3b3b3, 0x5100e5 });

			if (++i >= times) break;
		}
		if (i == 0) return true;

		return false;
	}

}
