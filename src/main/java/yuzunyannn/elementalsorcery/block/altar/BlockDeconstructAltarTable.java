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
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructAltarTable;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockDeconstructAltarTable extends BlockContainerNormal {
	public BlockDeconstructAltarTable() {
		super(Material.ROCK, "deconstructAltarTable", 5.5F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDeconstructAltarTable();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand, false);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		BlockHelper.dropWithIGetItemStack(worldIn, pos, state);
		super.breakBlock(worldIn, pos, state);
	}
}
