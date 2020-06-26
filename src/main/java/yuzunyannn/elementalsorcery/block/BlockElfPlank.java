package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

public class BlockElfPlank extends Block implements Mapper {

	public static final PropertyEnum<BlockElfPlank.EnumType> VARIANT = PropertyEnum.<BlockElfPlank.EnumType>create(
			"variant", BlockElfPlank.EnumType.class);

	public BlockElfPlank() {
		super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
		this.setUnlocalizedName("elfPlank");
		this.setHardness(2.5f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockElfPlank.EnumType.NORMAL));
		Blocks.FIRE.setFireInfo(this, 5, 20);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (BlockElfPlank.EnumType type : BlockElfPlank.EnumType.values())
			items.add(new ItemStack(this, 1, type.getMetadata()));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, BlockElfPlank.EnumType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT });
	}

	@Override
	public String apply(ItemStack var1) {
		return BlockElfPlank.EnumType.byMetadata(var1.getMetadata()).getName();
	}

	static public enum EnumType implements IStringSerializable {
		NORMAL("normal"), DARK("dark");

		final String name;

		EnumType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return this.ordinal();
		}

		static public EnumType byMetadata(int meta) {
			return EnumType.values()[meta];
		}
	}

}
