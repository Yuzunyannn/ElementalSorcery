package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.md.TileMDLiquidizer;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class BlockMDLiquidizer extends BlockMDBase {

	public BlockMDLiquidizer() {
		super("MDLiquidize");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDLiquidizer();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileMDLiquidizer tile = BlockHelper.getTileEntity(worldIn, pos, TileMDLiquidizer.class);
		if (tile == null) return false;
		ItemStack stack = playerIn.getHeldItem(hand);
		if (tile.addIngredient(stack, false)) {
			if (!EntityHelper.isCreative(playerIn)) stack.shrink(1);
			return true;
		}
		return false;
	}
}
