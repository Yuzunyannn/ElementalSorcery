package yuzunyannn.elementalsorcery.block.container;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.TileSmeltBox;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockSmeltBox extends BlockContainer {

	public final BlockHearth.EnumMaterial material;

	public static final PropertyBool BURNING = PropertyBool.create("burning");
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockSmeltBox(BlockHearth.EnumMaterial material) {
		super(Material.ROCK);
		this.material = material;
		this.setTranslationKey("smeltBox." + material.getName());
		this.setHarvestLevel("pickaxe", 1);
		switch (material) {
		case IRON:
			this.setHardness(5.0F);
			break;
		case KYANITE:
			this.setHardness(10.0F);
			break;
		default:
			this.setHardness(3.5F);
			break;
		}
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BURNING,
				Boolean.FALSE));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSmeltBox();
	}

	// 点击
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.DOWN) return false;
		if (worldIn.isRemote) return true;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_SMELT_BOX, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return true;
	}

	// 放置判断方向
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
	}

	// 方块被破坏
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileSmeltBox && !worldIn.isRemote) {
			IItemHandler item_handler;
			item_handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
			BlockHelper.drop(item_handler, worldIn, pos);
			item_handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
			BlockHelper.drop(item_handler, worldIn, pos);
			BlockHelper.drop(((TileSmeltBox) tileentity).getExtraItemStackHandler(), worldIn, pos);
		}
		super.breakBlock(worldIn, pos, state);
	}

	// 光亮程度
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(BURNING)) { return 8; }
		return 0;
	}

	// 初始化
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, BURNING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byHorizontalIndex(meta & 3);
		Boolean burning = Boolean.valueOf((meta & 8) != 0);
		return this.getDefaultState().withProperty(BURNING, burning).withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int facing = state.getValue(FACING).getHorizontalIndex();
		int burning = state.getValue(BURNING).booleanValue() ? 8 : 0;
		return burning | facing;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {

		if (stateIn.getValue(BURNING)) {
			EnumFacing enumfacing = (EnumFacing) stateIn.getValue(FACING);
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
			double d2 = (double) pos.getZ() + 0.5D;
			double d3 = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;
			double d5;

			switch (enumfacing) {
			case WEST:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
				// worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52D,
				// d1, d2 + d4, 0.0D, 0.0D, 0.0D);
				break;
			case EAST:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
				// worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52D,
				// d1, d2 + d4, 0.0D, 0.0D, 0.0D);
				break;
			case NORTH:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
				// worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1,
				// d2 - 0.52D, 0.0D, 0.0D, 0.0D);
				break;
			case SOUTH:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
				// worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1,
				// d2 + 0.52D, 0.0D, 0.0D, 0.0D);
				break;
			default:
				break;
			}

			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TileSmeltBox) {
				TileSmeltBox box = (TileSmeltBox) tile;
				if (!box.canUseExtraItem()) return;
				ItemStack stack = box.getExtraItemStackHandler().getStackInSlot(0);
				if (stack.isEmpty()) return;
				Item item = stack.getItem();
				if (item == Items.ENDER_EYE || item == ESObjects.ITEMS.MAGICAL_ENDER_EYE) {
					for (int i = 0; i < 3; ++i) {
						int j = rand.nextInt(2) * 2 - 1;
						int k = rand.nextInt(2) * 2 - 1;
						d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
						d1 = (double) ((float) pos.getY() + rand.nextFloat());
						d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
						d3 = (double) (rand.nextFloat() * (float) j);
						d4 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
						d5 = (double) (rand.nextFloat() * (float) k);
						worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
					}
				}
			}

		}
	}
}
