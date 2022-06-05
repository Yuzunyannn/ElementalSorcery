package yuzunyannn.elementalsorcery.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockContainerNormal extends BlockContainer {

	private static ThreadLocal<TileEntity> tileTemp = new ThreadLocal();

	public static void setDropTile(TileEntity tile) {
		tileTemp.set(tile);
	}

	public static TileEntity getDropTile(IBlockAccess world, BlockPos pos) {
		TileEntity temp = tileTemp.get();
		if (temp != null && temp.getPos().equals(pos)) return temp;
		return world.getTileEntity(pos);
	}

	protected BlockContainerNormal(Material materialIn) {
		super(materialIn);
	}

	protected BlockContainerNormal(Material materialIn, String unlocalizedName, float hardness, MapColor color) {
		super(materialIn, color);
		this.setTranslationKey(unlocalizedName);
		this.setHardness(hardness);
		if (materialIn == Material.WOOD) this.setHarvestLevel("axe", 1);
		else this.setHarvestLevel("pickaxe", 1);
		useNeighborBrightness = true;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tileEntiy,
			ItemStack stack) {
		BlockContainerNormal.setDropTile(tileEntiy);
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.005F);

		if (this.canSilkHarvest(worldIn, pos, state, player)
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
			ItemStack itemstack = this.getSilkTouchDrop(state);
			if (!itemstack.isEmpty()) {
				if (tileEntiy != null) writeTileDataToItemStack(worldIn, pos, player, tileEntiy, itemstack);
				items.add(itemstack);
			}
			ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
			for (ItemStack item : items) spawnAsEntity(worldIn, pos, item);
		} else {
			harvesters.set(player);
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
			this.dropBlockAsItem(worldIn, pos, state, i);
			harvesters.set(null);
		}

		BlockContainerNormal.setDropTile(null);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		Random rand = world instanceof World ? ((World) world).rand : RANDOM;
		int count = quantityDropped(state, fortune, rand);
		TileEntity tileEntiy = getDropTile(world, pos);
		for (int i = 0; i < count; i++) {
			Item item = this.getItemDropped(state, rand, fortune);
			if (item != Items.AIR) {
				ItemStack stack = new ItemStack(item, 1, this.damageDropped(state));
				if (tileEntiy != null) writeTileDataToItemStack(world, pos, harvesters.get(), tileEntiy, stack);
				drops.add(stack);
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null) {
			readTileDataFromItemStack(worldIn, pos, placer, tile, stack);
			onTileBlockPlacedBy(worldIn, pos, state, placer, stack, tile);
		}
	}

	public void writeTileDataToItemStack(IBlockAccess world, BlockPos pos, @Nullable EntityLivingBase user,
			TileEntity tile, ItemStack stack) {
		if (tile instanceof IWorldNameable) {
			if (((IWorldNameable) tile).hasCustomName()) stack.setStackDisplayName(((IWorldNameable) tile).getName());
		}
	}

	public void readTileDataFromItemStack(IBlockAccess world, BlockPos pos, @Nullable EntityLivingBase user,
			TileEntity tile, ItemStack stack) {

	}

	public void onTileBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack, TileEntity tile) {
	}

}
