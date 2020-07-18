package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.md.TileMDResonantIncubator;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockMDResonantIncubator extends BlockMDBase {

	public BlockMDResonantIncubator() {
		super("MDResonantIncubator");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDResonantIncubator();
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
