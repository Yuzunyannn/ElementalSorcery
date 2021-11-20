package yuzunyannn.elementalsorcery.api.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockJumpModify {

	void onPlayerJump(World world, BlockPos pos, IBlockState state, EntityLivingBase entity);

}
