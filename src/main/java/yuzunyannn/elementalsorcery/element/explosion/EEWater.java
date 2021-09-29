package yuzunyannn.elementalsorcery.element.explosion;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class EEWater extends ElementExplosion {

	public EEWater(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
	}

	@Override
	protected void doExplosionBlockAt(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (state.getMaterial() == Material.AIR) {
			if (!world.isRemote && world.rand.nextInt(20) == 0) {
				world.setBlockState(pos, Blocks.WATER.getDefaultState());
			}
			return;
		}

		if (world.isRemote) spawnEffectFromBlock(pos);
		else {
			if (block.canDropFromExplosion(vest))
				block.dropBlockAsItemWithChance(world, pos, state, 1.0F / this.size, 0);
			block.onBlockExploded(world, pos, vest);
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
		}

	}

	@Override
	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {
		super.doExplosionEntityAt(entity, orient, strength, damage * 0.05f, pound);
	}

}
