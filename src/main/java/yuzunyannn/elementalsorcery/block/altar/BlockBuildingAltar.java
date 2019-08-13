package yuzunyannn.elementalsorcery.block.altar;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.altar.TileBuildingAltar;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockBuildingAltar extends BlockContainer {

	public BlockBuildingAltar() {
		super(Material.ROCK);
		this.setUnlocalizedName("buildingAltar");
		this.setHardness(6.5F);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileBuildingAltar();
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
		return BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand, false);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		BlockHelper.dropWithIGetItemStack(worldIn, pos, state);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileBuildingAltar && !worldIn.isRemote) {
			if (((TileBuildingAltar) tileentity).isWorking())
				((TileBuildingAltar) tileentity).badEnd();
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		if (face == EnumFacing.DOWN)
			return BlockFaceShape.SOLID;
		return BlockFaceShape.UNDEFINED;
	}
}
