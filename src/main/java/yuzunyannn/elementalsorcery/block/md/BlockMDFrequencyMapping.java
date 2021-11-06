package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.md.TileMDFrequencyMapping;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockMDFrequencyMapping extends BlockMDBase {

	public BlockMDFrequencyMapping() {
		super("MDFrequencyMapping");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDFrequencyMapping();
	}

	@Override
	protected int guiId() {
		return 0;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand, true);
	}
}
