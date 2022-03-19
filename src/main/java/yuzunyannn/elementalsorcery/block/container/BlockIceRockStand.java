package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.item.ItemEntangleNode;
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

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		ItemStack stack = playerIn.getHeldItem(hand);
		ItemStack entangleNode = ItemStack.EMPTY;
		if (stack.isEmpty()) entangleNode = ItemEntangleNode.create(pos);
		else {
			BlockPos corePos = ItemEntangleNode.getBlockPos(stack);
			if (corePos != null) {
				entangleNode = stack;
				ItemEntangleNode.setBlockPos(entangleNode, pos);
			}
		}
		if (entangleNode.isEmpty()) return false;
		TileIceRockStand tile = BlockHelper.getTileEntity(worldIn, pos, TileIceRockStand.class);
		if (tile == null) return false;
		if (tile.getLinkCount() <= 0) return false;

		playerIn.setHeldItem(hand, entangleNode);
		return true;
	}

}
