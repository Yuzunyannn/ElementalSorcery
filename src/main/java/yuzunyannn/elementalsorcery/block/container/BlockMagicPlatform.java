package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.block.container.BlockHearth.EnumMaterial;
import yuzunyannn.elementalsorcery.tile.TileMagicPlatform;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class BlockMagicPlatform extends BlockContainer implements Mapper {

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 5.0 / 16.0, 1.0D);
	public static final PropertyEnum<EnumMaterial> MATERIAL = PropertyEnum.create("material", EnumMaterial.class);

	public BlockMagicPlatform() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 1);
		this.setUnlocalizedName("magicPlatform");
		this.setHardness(3.5f);
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, MATERIAL);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMagicPlatform();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand, true);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		BlockHelper.dropWithIGetItemStack(worldIn, pos, state);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(MATERIAL).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumMaterial material = EnumMaterial.values()[meta & 3];
		return this.getDefaultState().withProperty(MATERIAL, material);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(MATERIAL).ordinal();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
	}

	public static enum EnumMaterial implements IStringSerializable {
		WOOD("wood"), ESTONE("estone");

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

	@Override
	public String apply(ItemStack var1) {
		return EnumMaterial.values()[var1.getMetadata() & 3].getName();
	}
}
