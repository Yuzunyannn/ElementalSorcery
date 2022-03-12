package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockStand;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockIceRockStand extends BlockContainerNormal {

	public static final int TOWER_MAX_HEIGHT = 8;

	public BlockIceRockStand() {
		super(Material.ROCK, "iceRockStand", 1.75F, MapColor.QUARTZ);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileIceRockStand();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		if (worldIn.isRemote) return;
		TileIceRockStand tile = BlockHelper.getTileEntity(worldIn, pos, TileIceRockStand.class);
		if (tile != null) tile.checkAndBuildStructure();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileIceRockStand tile = BlockHelper.getTileEntity(worldIn, pos, TileIceRockStand.class);
		if (tile != null) tile.checkAndBreakStructure();
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}
	
	

}
