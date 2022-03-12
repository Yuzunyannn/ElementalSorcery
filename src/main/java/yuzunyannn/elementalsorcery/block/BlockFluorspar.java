package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFluorspar extends Block {

	public ItemBlock getItemBlock() {
		return new ItemMultiTexture(this, this, (stack) -> "") {
			@Override
			public String getTranslationKey(ItemStack stack) {
				String name = BlockFluorspar.EnumType.byMetadata(stack.getMetadata()).getTranslationKey();
				return "tile." + name;
			}

			@Override
			public String getItemStackDisplayName(ItemStack stack) {
				return I18n.translateToLocal("tile.fluorspar.name").trim() + super.getItemStackDisplayName(stack);
			}
		};
	}

	public static final PropertyEnum<BlockFluorspar.EnumType> VARIANT = PropertyEnum.<BlockFluorspar.EnumType>create(
			"variant", BlockFluorspar.EnumType.class);

	public BlockFluorspar() {
		super(Material.ROCK);
		this.setHardness(1).setLightLevel(1.0F);
		this.setTranslationKey("fluorspar");
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockFluorspar.EnumType.STONE));
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		EnumType type = state.getValue(VARIANT);
		if (type == EnumType.DIRT) return SoundType.PLANT;
		return super.getSoundType(state, world, pos, entity);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		return blockState.getValue(VARIANT).getHardness();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, BlockFluorspar.EnumType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (BlockFluorspar.EnumType type : BlockFluorspar.EnumType.values())
			items.add(new ItemStack(this, 1, type.getMetadata()));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT });
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
	}

	static public enum EnumType implements IStringSerializable {
		STONE("stone", "stone.stone", 1.5f),
		COBBLESTONE("cobblestone", "stonebrick", 2.0f),
		DIRT("dirt", "dirt", 0.5f),
		NETHERRACK("netherrack", "hellrock", 0.4f),
		ANDESITE("andesite", "stone.andesite", 1.5f),
		GRANITE("granite", "stone.granite", 1.5f),
		DIORITE("diorite", "stone.diorite", 1.5f);

		final String name;
		final String unlocalizedName;
		final float hardness;

		EnumType(String name, String unlocalizedName, float hardness) {
			this.name = name;
			this.hardness = hardness;
			this.unlocalizedName = unlocalizedName;
		}

		@Override
		public String getName() {
			return name;
		}

		public String getTranslationKey() {
			return unlocalizedName;
		}

		public float getHardness() {
			return hardness;
		}

		public int getMetadata() {
			return this.ordinal();
		}

		static public EnumType byMetadata(int meta) {
			return EnumType.values()[0x7 & meta];
		}
	}

}
