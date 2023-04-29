package yuzunyannn.elementalsorcery.block.env;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonPropBase;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public abstract class BlockDungeonPropBase extends Block implements ITileEntityProvider {

	public BlockDungeonPropBase(Material materialIn) {
		super(materialIn);

	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		if (!ESAPI.isDevelop) return;
		super.getSubBlocks(itemIn, items);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		TileDungeonPropBase tile = BlockHelper.getTileEntity(worldIn, pos, TileDungeonPropBase.class);
		if (tile != null) return tile.onActivated(playerIn, hand, facing, hitX, hitY, hitZ);
		return true;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
			ItemStack stack) {
		if (te instanceof TileDungeonPropBase) {
			BlockContainerNormal.setDropTile(te);
			((TileDungeonPropBase) te).onHarvest(player);
		}
		super.harvestBlock(worldIn, player, pos, state, te, stack);
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		TileDungeonPropBase tile = BlockHelper.getTileEntity(world, pos, TileDungeonPropBase.class);
		if (tile != null) tile.onDestroyed();
		super.onBlockExploded(world, pos, explosion);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		TileDungeonPropBase tile = BlockContainerNormal.getDropTile(world, pos, TileDungeonPropBase.class);
		if (tile != null) tile.onDrops(drops);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (worldIn.isRemote) return;

		TileDungeonPropBase tile = BlockHelper.getTileEntity(worldIn, pos, TileDungeonPropBase.class);
		if (tile == null) {
			worldIn.destroyBlock(pos, false);
			return;
		}

		tile.onEntityCollision(entityIn);
		super.onEntityCollision(worldIn, pos, state, entityIn);
	}

}
