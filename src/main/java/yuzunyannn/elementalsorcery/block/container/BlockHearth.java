package yuzunyannn.elementalsorcery.block.container;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.TileHearth;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockHearth extends BlockContainer implements Mapper {

	public static final PropertyBool BURNING = PropertyBool.create("burning");
	public static final PropertyEnum<EnumMaterial> MATERIAL = PropertyEnum.create("material", EnumMaterial.class);
	public static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

	public BlockHearth() {
		super(Material.ROCK);
		this.setTranslationKey("hearth");
		this.setHarvestLevel("pickaxe", 1);
		this.setHardness(3.5F);
		this.setLightOpacity(255);
		this.setDefaultState(this.blockState.getBaseState().withProperty(BURNING, false).withProperty(MATERIAL,
				EnumMaterial.COBBLESTONE));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BLOCK_AABB;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileHearth();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) BlockHelper.drop(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) return false;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_HEARTH, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return true;

	}

	// 光亮程度
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(BURNING)) { return 12; }
		return 0;
	}

	// 硬度获取
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		EnumMaterial material = blockState.getValue(MATERIAL);
		switch (material) {
		case COBBLESTONE:
			return 3.5F;
		case IRON:
			return 5.0F;
		case KYANITE:
			return 10.0F;
		default:
			break;
		}
		// 为啥这个函数要被删除了。。
		return super.getBlockHardness(blockState, worldIn, pos);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	// 不是完成方块哟
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	// 是透明方块哟
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	// 创建新的属性
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BURNING, MATERIAL);
	}

	// 掉落时候的meta值
	@Override
	public int damageDropped(IBlockState state) {
		// 灶台没有方向，因此4位可以全作为材质
		return state.getValue(MATERIAL).ordinal();
	}

	// 从meta回复
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumMaterial material = EnumMaterial.values()[meta & 3];
		Boolean burning = Boolean.valueOf((meta & 8) != 0);
		return this.getDefaultState().withProperty(BURNING, burning).withProperty(MATERIAL, material);
	}

	// 将状态转成meta（为毛只有四位）
	@Override
	public int getMetaFromState(IBlockState state) {
		int material = state.getValue(MATERIAL).ordinal();
		int burning = state.getValue(BURNING).booleanValue() ? 8 : 0;
		return burning | material;
	}

	public static enum EnumMaterial implements IStringSerializable {
		COBBLESTONE("cobblestone"), IRON("iron"), KYANITE("kyanite");

		private String name;

		private EnumMaterial(String material) {
			this.name = material;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	public static String metaToUnlocalizedName(int meta) {
		return "tile.hearth." + EnumMaterial.values()[meta & 3].getName() + ".name";
	}

	@Override
	public String apply(ItemStack var1) {
		return EnumMaterial.values()[var1.getMetadata() & 3].getName();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
		items.add(new ItemStack(this, 1, 2));
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.getValue(BURNING)) BlockHearth.displayTick(worldIn, pos, rand);
	}

	public static void displayTick(World worldIn, BlockPos pos, Random rand) {
		double x = (double) pos.getX() + 0.25D + rand.nextDouble() * 0.5D;
		double y = (double) pos.getY() + 0.75D + rand.nextDouble() * 0.25D;
		double z = (double) pos.getZ() + 0.25D + rand.nextDouble() * 0.5D;

		if (rand.nextDouble() < 0.1D) {
			worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D,
					SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
		}
		worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
		worldIn.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
	}
}
