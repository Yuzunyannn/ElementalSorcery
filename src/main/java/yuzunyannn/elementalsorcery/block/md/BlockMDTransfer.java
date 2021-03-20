package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.md.TileMDFrequencyMapping;
import yuzunyannn.elementalsorcery.tile.md.TileMDTransfer;

public class BlockMDTransfer extends BlockMDBase {

	public BlockMDTransfer() {
		super("MDTransfer");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDTransfer();
	}

	@Override
	protected int guiId() {
		return 0;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return false;
	}
}
