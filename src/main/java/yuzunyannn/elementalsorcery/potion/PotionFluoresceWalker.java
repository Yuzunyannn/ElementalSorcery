package yuzunyannn.elementalsorcery.potion;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFluorspar;

public class PotionFluoresceWalker extends PotionCommon {

	public PotionFluoresceWalker() {
		super(false, 0xeafdff, "fluoresceWalker");
		iconIndex = 4;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		if (duration % 10 != 0) return false;
		return Math.random() < 0.06f * Math.pow(amplifier + 1, 1.2f);
	}

	@Override
	public void performEffect(EntityLivingBase owner, int amplifier) {
		World world = owner.world;
		if (world.isRemote) return;

		BlockPos pos = new BlockPos(owner.posX, owner.posY - 0.5, owner.posZ);
		IBlockState originState = world.getBlockState(pos);
		IBlockState state = MantraFluorspar.getChange(originState);
		if (state == null) return;

		if (originState.getBlock() == Blocks.STONE && amplifier >= 2) {
			if (Math.random() < 0.01f) {
				state = Blocks.IRON_ORE.getDefaultState();
			}
		}
		world.setBlockState(pos, state);
	}

}
