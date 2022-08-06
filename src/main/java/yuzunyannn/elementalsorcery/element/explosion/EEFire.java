package yuzunyannn.elementalsorcery.element.explosion;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireBall;

public class EEFire extends ElementExplosion {

	public EEFire(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
	}

	@Override
	protected float getExplosionResistance(BlockPos pos, IBlockState state) {
		return super.getExplosionResistance(pos, state) * 0.1f;
	}

	@Override
	protected void doExplosionBlockAt(BlockPos pos) {

		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() == Material.AIR) return;

		if (world.isRemote) spawnEffectFromBlock(pos);
		else {
			BlockPos center = new BlockPos(position);
			IBlockState newBlockState = MantraFireBall.affect(world, center, pos.subtract(center), null);
			IBlockState lavaState = Blocks.FLOWING_LAVA.getDefaultState().withProperty(BlockFluidBase.LEVEL, 15);
			if (newBlockState != null) world.setBlockState(pos, newBlockState);
			if (world.rand.nextInt(5) == 0) world.setBlockState(pos, lavaState);
		}
	}

	@Override
	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {
		float fireDamage = MathHelper.sqrt(eStack.getPower() / 20);
		super.doExplosionEntityAt(entity, orient, strength, damage + fireDamage, pound);
	}

}
