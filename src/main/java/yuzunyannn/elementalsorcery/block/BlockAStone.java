package yuzunyannn.elementalsorcery.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.item.IItemSmashable;

public class BlockAStone extends Block implements Mapper {

	public class ItemAStone extends ItemMultiTexture implements IItemSmashable {

		public ItemAStone() {
			super(BlockAStone.this, BlockAStone.this, BlockAStone.this);
		}

		@Override
		public void doSmash(World world, Vec3d vec, ItemStack stack, List<ItemStack> outputs, Entity operator) {
			if (world.isRemote) return;
			if (stack.getMetadata() == EnumType.FRAGMENTED.ordinal()) return;
			stack.setItemDamage(EnumType.FRAGMENTED.ordinal());
		}
	}

	public ItemBlock getItemBlock() {
		return new ItemAStone();
	}

	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

	public BlockAStone() {
		super(Material.ROCK, MapColor.BLUE);
		this.setTranslationKey("astone");
		this.setHarvestLevel("pickaxe", 1);
		this.setHardness(10F);
		this.setDefaultState(this.getDefaultState().withProperty(VARIANT, EnumType.STONE));
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

	public static enum EnumType implements IStringSerializable {
		STONE("stone"),
		FRAGMENTED("fragmented"),
		SMOOTH("smooth"),
		VEIN("vein"),
		CIRCLE("circle"),
		BRICK("brick"),
		TRANS("trans");

		final String name;

		EnumType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

	}
}
