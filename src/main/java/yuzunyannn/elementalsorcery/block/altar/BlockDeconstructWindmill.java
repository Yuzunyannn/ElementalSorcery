package yuzunyannn.elementalsorcery.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructWindmill;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockDeconstructWindmill extends BlockContainerNormal {

	public BlockDeconstructWindmill() {
		super(Material.ROCK, "deconstructWindmill", 5.5F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDeconstructWindmill();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand, true);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		BlockHelper.dropWithGetItemStack(worldIn, pos, state);
		super.breakBlock(worldIn, pos, state);
	}
}
