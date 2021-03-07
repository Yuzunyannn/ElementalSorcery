package yuzunyannn.elementalsorcery.block.md;

import java.util.Random;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.block.container.BlockHearth;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDHearth;

public class BlockMDHearth extends BlockMDBase {

	public BlockMDHearth() {
		super("MDHearth");
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHearth.BURNING, false));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BlockHearth.BLOCK_AABB;
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockHearth.BURNING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BlockHearth.BURNING, meta != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockHearth.BURNING) ? 1 : 0;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(BlockHearth.BURNING)) return 12;
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDHearth();
	}

	@Override
	protected int guiId() {
		return ESGuiHandler.GUI_MD_HEARTH;
	}

	@Override
	protected boolean canOpenGUI(World worldIn, EnumFacing facing, float hitX, float hitY, float hitZ,
			ItemStack stack) {
		return facing != EnumFacing.UP;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.getValue(BlockHearth.BURNING)) BlockHearth.displayTick(worldIn, pos, rand);
	}

}
