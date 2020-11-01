package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.SoundType;
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
import yuzunyannn.elementalsorcery.tile.TileElfBeacon;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockElfBeacon extends BlockContainerNormal {

	public BlockElfBeacon() {
		super(Material.GLASS, "elfBeacon", 5.5f);
		this.setLightLevel(0.5f);
		this.setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileElfBeacon();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (worldIn.isRemote) return;
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileElfBeacon) ((TileElfBeacon) tile).onBreak();
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand, true);
	}

}
