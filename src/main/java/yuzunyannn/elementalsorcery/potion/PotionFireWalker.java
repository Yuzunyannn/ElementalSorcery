package yuzunyannn.elementalsorcery.potion;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class PotionFireWalker extends PotionCommon {

	public PotionFireWalker() {
		super(false, 0xd38409, "fireWalker");
		iconIndex = 1;

		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "26ee6c1d-e2b9-44ed-ab64-10de3e407e48",
				0.04, 2);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return Math.random() < 0.05f * (amplifier + 1);
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		if (entity.world.isRemote) return;
		World world = entity.world;
		BlockPos pos = entity.getPosition();
		if (BlockHelper.isReplaceBlock(world, pos) && !BlockHelper.isReplaceBlock(world, pos.down()))
			world.setBlockState(pos, Blocks.FIRE.getDefaultState());
		entity.setFire(amplifier * 10 * 20 + 60);
		if (amplifier < 2) return;

		pos = pos.down();
		Random rand = entity.getRNG();
		int range = (int) Math.sqrt(amplifier - 1) + 1;
		IBlockState fire = Blocks.FIRE.getDefaultState();

		for (int i = 0; i < amplifier - 1; i++) {
			BlockPos at = pos.add(rand.nextGaussian() * range, -2, rand.nextGaussian() * range);
			if (!world.isAirBlock(at)) {
				for (int j = 0; j < 5; j++) {
					at = at.up();
					if (world.isAirBlock(at)) {
						world.setBlockState(at, fire);
						break;
					}
				}
			}
		}
	}

}
