package yuzunyannn.elementalsorcery.block.env;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

public class BlockDungeonBrick extends Block implements Mapper {

	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

	public static enum EnumType implements IStringSerializable {
		DEFAULT(0, "default"),
		MOSSY(1, "mossy"),
		CRACKED(2, "cracked"),
		CHISELED(3, "chiseled"),
		STONE(4, "stone");

		final String name;
		final int meta;

		EnumType(int meta, String name) {
			this.meta = meta;
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMeta() {
			return meta;
		}

	}

	public BlockDungeonBrick() {
		super(Material.ROCK);
		this.setTranslationKey("dungeonBrick");
		this.setSoundType(SoundType.STONE);
		this.setHardness(-1);
		this.setDefaultState(this.getDefaultState().withProperty(VARIANT, EnumType.DEFAULT));
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT });
	}

	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (EnumType enumtype : EnumType.values()) items.add(new ItemStack(this, 1, enumtype.ordinal()));
	}

	@Override
	public String apply(ItemStack var1) {
		return EnumType.values()[var1.getItemDamage()].getName();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumType) state.getValue(VARIANT)).ordinal();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumType) state.getValue(VARIANT)).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumType.values()[meta]);
	}

}
