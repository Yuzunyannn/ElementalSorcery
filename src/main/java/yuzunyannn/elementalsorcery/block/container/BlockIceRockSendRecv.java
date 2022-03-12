package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockSendRecv;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public abstract class BlockIceRockSendRecv extends BlockContainerNormal {

	protected BlockIceRockSendRecv(Material materialIn, String unlocalizedName, float hardness, MapColor color) {
		super(materialIn, unlocalizedName, hardness, color);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		notifyCheckFacing(worldIn, pos, fromPos);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(world, pos, neighbor);
		notifyCheckFacing(world, pos, neighbor);
	}

	public void notifyCheckFacing(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileIceRockSendRecv tile = BlockHelper.getTileEntity(world, pos, TileIceRockSendRecv.class);
		if (tile == null) return;
		EnumFacing facing = EnumFacing.getFacingFromVector(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(),
				neighbor.getZ() - pos.getZ());
		tile.checkFaceChange(facing);
	}

}
