package yuzunyannn.elementalsorcery.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileElementTranslocator;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockElementTranslocator extends BlockContainerNormal {

	public BlockElementTranslocator() {
		super(Material.ROCK, "elementTranslocator", 5.5f);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileElementTranslocator();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.DOWN) return false;
		if (worldIn.isRemote) return true;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ELEMENT_TRANSLOCATOR, worldIn, pos.getX(),
				pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileElementTranslocator tile = BlockHelper.getTileEntity(worldIn, pos, TileElementTranslocator.class);
		if (tile != null) BlockHelper.drop(tile.getItemStackHandler(), worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}

}
