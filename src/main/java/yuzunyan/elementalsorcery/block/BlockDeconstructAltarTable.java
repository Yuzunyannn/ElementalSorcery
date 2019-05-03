package yuzunyan.elementalsorcery.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.tile.TileDeconstructAltarTable;
import yuzunyan.elementalsorcery.util.block.BlockHelper;

public class BlockDeconstructAltarTable extends BlockContainer {
	public BlockDeconstructAltarTable() {
		super(Material.ROCK);
		this.setUnlocalizedName("deconstructAltarTable");
		this.setHardness(5.5F);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDeconstructAltarTable();
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand,false);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		BlockHelper.breakBlockWithIGetItemStack(worldIn, pos, state);
		super.breakBlock(worldIn, pos, state);
	}
}
