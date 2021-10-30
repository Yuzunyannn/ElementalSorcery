package yuzunyannn.elementalsorcery.potion;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class PotionTideWalker extends PotionCommon {

	public PotionTideWalker() {
		super(false, 0x2f43f4, "tideWalker");
		iconIndex = 2;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		World world = entity.world;
		if (entity.isInWater()) {
			if (entity.motionY < 0) entity.motionY = 0;
			amplifier = Math.min(5, amplifier);
			entity.motionX *= (1.2 + 0.005f * amplifier);
			entity.motionZ *= (1.2 + 0.005f * amplifier);
		} else {
			if (!world.isRemote) {
				BlockPos pos = entity.getPosition().down();
				tryChange(world, pos);
				if (amplifier >= 1) {
					for (EnumFacing facing : EnumFacing.HORIZONTALS) tryChange(world, pos.offset(facing));
				}
			}
		}
		entity.extinguish();
		if (entity.world.isRemote) {
			Random rand = RandomHelper.rand;
			entity.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, entity.posX + rand.nextGaussian(), entity.posY,
					entity.posZ + rand.nextGaussian(), 0.0D, 0.0D, 0.0D);
		}
	}

	protected void tryChange(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() != Material.LAVA) return;
		if (state.getValue(BlockFluidBase.LEVEL) == 0) world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
		else world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
	}

}
