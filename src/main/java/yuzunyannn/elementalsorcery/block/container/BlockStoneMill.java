package yuzunyannn.elementalsorcery.block.container;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.item.tool.ItemMillHammer;
import yuzunyannn.elementalsorcery.tile.TileStoneMill;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class BlockStoneMill extends BlockContainerNormal {

	public static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);
	public static final AxisAlignedBB AABB_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);
	public static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
	public static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
	public static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	public static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

	public BlockStoneMill() {
		super(Material.ROCK, "stoneMill", 3.5F, MapColor.STONE);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileStoneMill();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BLOCK_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BOTTOM);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_WEST);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_NORTH);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_EAST);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_SOUTH);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileStoneMill) {
			TileStoneMill mill = ((TileStoneMill) tileentity);
			ItemStack hammer = mill.getHammer();
			ItemStack hold = playerIn.getHeldItem(hand);
			if (hammer.isEmpty()) {
				if (hold.getItem() == ESObjects.ITEMS.MILL_HAMMER) {
					if (worldIn.isRemote) return true;
					mill.setHammer(hold);
					mill.updateToClient();
					playerIn.setHeldItem(hand, ItemStack.EMPTY);
					return true;
				}
				return false;
			} else {
				if (playerIn.isSneaking()) {
					if (mill.isEmpty() && hold.isEmpty()) {
						if (worldIn.isRemote) return true;
						mill.setHammer(ItemStack.EMPTY);
						mill.updateToClient();
						ItemHelper.addItemStackToPlayer(playerIn, hammer);
					} else mill.millDrop();
				} else mill.mill();
				return true;
			}
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (!worldIn.isRemote && tileentity instanceof TileStoneMill) {
			((TileStoneMill) tileentity).drop();
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity.world.isRemote) return;
		if (entity instanceof EntityItem) {
			if (entity.posY <= pos.getY() + 1) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile instanceof TileStoneMill) ((TileStoneMill) tile).eatItem((EntityItem) entity);
			}
		}
	}

	public static boolean hasHammer(ItemStack stack) {
		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
		int damage = nbt.getInteger("hammer");
		if (damage < 0) return false;
		return true;
	}

	public static ItemStack getHammer(ItemStack stack) {
		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
		int damage = nbt.getInteger("hammer");
		if (damage < 0) return ItemStack.EMPTY;
		ItemStack hammer = new ItemStack(ESObjects.ITEMS.MILL_HAMMER, 1, damage);
		ItemMillHammer.onCreateMillHammer(hammer);
		return hammer;
	}

	public static void setHammer(ItemStack stack, ItemStack hanmmer) {
		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
		if (hanmmer.isEmpty()) nbt.setShort("hammer", (short) -1);
		else {
			if (hanmmer.getItemDamage() == 0) nbt.removeTag("hammer");
			else nbt.setShort("hammer", (short) hanmmer.getItemDamage());
		}
	}

	@Override
	public void readTileDataFromItemStack(IBlockAccess world, BlockPos pos, @Nullable EntityLivingBase user,
			TileEntity tile, ItemStack stack) {
		super.readTileDataFromItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileStoneMill) {
			ItemStack hammer = getHammer(stack);
			((TileStoneMill) tile).setHammer(hammer);
		}
	}

	@Override
	public void writeTileDataToItemStack(IBlockAccess world, BlockPos pos, @Nullable EntityLivingBase user,
			TileEntity tile, ItemStack stack) {
		super.writeTileDataToItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileStoneMill) {
			setHammer(stack, ((TileStoneMill) tile).getHammer());
		}
	}

}
