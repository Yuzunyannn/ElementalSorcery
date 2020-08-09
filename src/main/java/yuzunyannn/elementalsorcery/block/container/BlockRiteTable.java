package yuzunyannn.elementalsorcery.block.container;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.ESCreativeTabs;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockRiteTable extends BlockContainerNormal {

	public ItemBlock getItemBlock() {
		ItemBlock item = new ItemBlock(this) {
			@Override
			public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				int level = 0;
				NBTTagCompound nbt = stack.getTagCompound();
				if (nbt != null) level = nbt.getInteger("level");
				tooltip.add(TextFormatting.YELLOW + I18n.format("info.level", level));
			}

			@Override
			public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
				if (tab != ESCreativeTabs.TAB) return;
				for (int i = 0; i <= TileRiteTable.MAX_LEVEL; i++) {
					ItemStack stack = new ItemStack(this);
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setInteger("level", i);
					stack.setTagCompound(nbt);
					items.add(stack);
				}
			}
		};
		return item;
	}

	public BlockRiteTable() {
		super(Material.ROCK, "riteTable", 5);
	}

	protected static final AxisAlignedBB AABB_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 10.0 / 16.0, 1.0D);

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BOX);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileRiteTable();
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		if (!super.canPlaceBlockAt(worldIn, pos)) return false;
		pos = pos.down();
		for (EnumFacing face : EnumFacing.HORIZONTALS) {
			IBlockState state = worldIn.getBlockState(pos.offset(face));
			if (state.getBlock() instanceof BlockTorch) continue;
			if (state.getBlock() instanceof BlockFlower) continue;
			if (!state.getBlock().isReplaceable(worldIn, pos.offset(face))) return false;
		}
		return true;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileRiteTable) {
			TileRiteTable trt = (TileRiteTable) tile;
			// 全部拿出来
			if (playerIn.isSneaking()) {
				boolean hasChange = false;
				ItemStackHandler inv = trt.getInventory();
				for (int i = 0; i < inv.getSlots(); i++) {
					stack = inv.getStackInSlot(i);
					if (stack.isEmpty()) continue;
					hasChange = true;
					if (!playerIn.inventory.addItemStackToInventory(stack)) Block.spawnAsEntity(worldIn, pos, stack);
				}
				if (!hasChange) return false;
				trt.updateToClient();
				return true;
			}
			// 取出
			if (stack.isEmpty()) {
				stack = trt.extract();
				if (stack.isEmpty()) return false;
				playerIn.setHeldItem(hand, stack);
				return true;
			}
			if (stack.getItem() == Items.WOODEN_SWORD) {
				if (stack.getItemDamage() > 0 && !playerIn.isCreative()) return false;
				if (trt.rite(playerIn)) {
					if (!playerIn.isCreative()) stack.setItemDamage(stack.getMaxDamage() / 2);
					playerIn.setHeldItem(hand, stack);
				} else return false;
			} else playerIn.setHeldItem(hand, trt.insert(stack));
			return true;
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileRiteTable) {
			int level = 0;
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt != null) level = nbt.getInteger("level");
			((TileRiteTable) tile).setLevel(level);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileRiteTable) {
			BlockHelper.drop(((TileRiteTable) tile).getInventory(), worldIn, pos);
			ItemStack drop = new ItemStack(this);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("level", ((TileRiteTable) tile).getLevel());
			drop.setTagCompound(nbt);
			Block.spawnAsEntity(worldIn, pos, drop);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		if (face == EnumFacing.UP) return BlockFaceShape.SOLID;
		return BlockFaceShape.UNDEFINED;
	}
}
