package yuzunyannn.elementalsorcery.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.block.BlockContainerNormal;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockAnalysisAltar extends BlockContainerNormal {

	public BlockAnalysisAltar() {
		super(Material.ROCK, "analysisAltar", 6.5F); 
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileAnalysisAltar();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.DOWN)
			return false;
		if (worldIn.isRemote)
			return true;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ANALYSIS_ALTAR, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileAnalysisAltar && !worldIn.isRemote) {
			IItemHandler itemHandler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
					EnumFacing.UP);
			BlockHelper.drop(itemHandler, worldIn, pos);
		}
		super.breakBlock(worldIn, pos, state);
	}
}
