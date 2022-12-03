package yuzunyannn.elementalsorcery.summon;

import java.util.AbstractMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicGuard;

public class SummonRelicGuard extends SummonRelicZombie {

	public SummonRelicGuard(World world, BlockPos pos) {
		super(world, pos, 0xfdffce);
	}

	@Override
	public void initData() {
		this.size = 4;
		this.height = 5;
		this.index = 0;
	}

	@Override
	protected void initBuilding() {
		IBlockState state = Blocks.SANDSTONE.getDefaultState();

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				build.add(new AbstractMap.SimpleEntry(pos.add(x, -1, z), state));
			}
		}

		build.add(new AbstractMap.SimpleEntry(pos.add(0, 0, 0), state));
		
		build.add(new AbstractMap.SimpleEntry(pos.add(2, 0, 2), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-2, 0, 2), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-2, 0, -2), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(2, 0, -2), state));

		build.add(new AbstractMap.SimpleEntry(pos.add(1, 0, 3), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(3, 0, -1), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-1, 0, -3), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-3, 0, 1), state));

		build.add(new AbstractMap.SimpleEntry(pos.add(-1, 0, 3), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(3, 0, 1), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(1, 0, -3), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-3, 0, -1), state));
	}

	@Override
	protected void finish() {
		if (world.isRemote) return;
		EntityRelicGuard zombie = new EntityRelicGuard(world);
		zombie.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
		world.spawnEntity(zombie);
	}

}
