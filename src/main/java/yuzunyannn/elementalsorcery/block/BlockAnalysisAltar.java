package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;

public class BlockAnalysisAltar extends BlockContainer {

	public BlockAnalysisAltar() {
		super(Material.ROCK);
		this.setUnlocalizedName("analysisAltar");
		this.setHardness(6.5F);
		this.setHarvestLevel("pickaxe", 1);
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
}
