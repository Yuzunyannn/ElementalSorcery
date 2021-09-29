package yuzunyannn.elementalsorcery.element.explosion;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class EEEarth extends ElementExplosion {

	public EEEarth(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
		passClientExplosionEntity = true;
	}

	@Override
	protected void doExplosionBlockAt(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (state.getMaterial() == Material.AIR) return;

		if (world.isRemote) spawnEffectFromBlock(pos);
		else {
			if (block.canDropFromExplosion(vest)) {
				block.dropBlockAsItem(world, pos, state, 0);
				world.setBlockToAir(pos);
			} else block.onBlockExploded(world, pos, vest);
		}
	}

	@Override
	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {
		if (entity instanceof EntityLivingBase) doDamageSource(entity, damage * 0.5f);
	}

}
