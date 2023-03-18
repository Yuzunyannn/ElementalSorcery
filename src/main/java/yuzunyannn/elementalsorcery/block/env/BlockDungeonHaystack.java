package yuzunyannn.elementalsorcery.block.env;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonHaystack;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockDungeonHaystack extends Block implements ITileEntityProvider {

	public BlockDungeonHaystack() {
		super(Material.PLANTS);
		this.setSoundType(SoundType.PLANT);
		this.setTranslationKey("dungeonHaystack");
		this.setHardness(0);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
			ItemStack stack) {
		if (te instanceof TileDungeonHaystack) {
			BlockContainerNormal.setDropTile(te);
			((TileDungeonHaystack) te).onSweepOpen(player);
		}
		super.harvestBlock(worldIn, player, pos, state, te, stack);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		TileDungeonHaystack tile = BlockHelper.getTileEntity(worldIn, pos, TileDungeonHaystack.class);
		worldIn.destroyBlock(pos, true);
		if (tile != null) tile.onSweepOpen(playerIn);
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileDungeonHaystack tile = BlockHelper.getTileEntity(source, pos, TileDungeonHaystack.class);
		if (tile != null) return tile.getBoundingBox();
		return super.getBoundingBox(state, source, pos);
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		TileDungeonHaystack tile = BlockHelper.getTileEntity(world, pos, TileDungeonHaystack.class);
		if (tile != null) tile.onSweepOpen(null);
		super.onBlockExploded(world, pos, explosion);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		TileDungeonHaystack tile = BlockContainerNormal.getDropTile(world, pos, TileDungeonHaystack.class);
		if (tile != null) tile.getDrops(drops);
		else drops.add(new ItemStack(Items.WHEAT));
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
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) || !worldIn.isAirBlock(pos.down());
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (worldIn.isAirBlock(pos.down())) worldIn.destroyBlock(pos, true);
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
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDungeonHaystack();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		if (!ESAPI.isDevelop) return;
		super.getSubBlocks(itemIn, items);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (worldIn.isRemote) return;

		if (entityIn instanceof EntityLivingBase) {
			if (entityIn instanceof IMob) return;
			if (entityIn instanceof IAnimals) return;
			TileDungeonHaystack tile = BlockHelper.getTileEntity(worldIn, pos, TileDungeonHaystack.class);
			if (tile == null) {
				worldIn.destroyBlock(pos, true);
				return;
			}
			if (!tile.getPressure()) return;
			worldIn.destroyBlock(pos, true);
			tile.onSweepOpen((EntityLivingBase) entityIn);
		}

		super.onEntityCollision(worldIn, pos, state, entityIn);
	}

}
