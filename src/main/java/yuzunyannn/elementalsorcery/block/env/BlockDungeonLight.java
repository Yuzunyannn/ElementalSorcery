package yuzunyannn.elementalsorcery.block.env;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;

public class BlockDungeonLight extends Block {

	public static final PropertyBool OPEN = PropertyBool.create("open");

	public BlockDungeonLight() {
		super(Material.GLASS);
		this.setTranslationKey("redstoneLight");
		this.setHardness(0.3F);
		this.setSoundType(SoundType.GLASS);
		this.setDefaultState(this.getDefaultState().withProperty(OPEN, false));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
		// 只在client修改，减少服务端计算，同时不会阻止刷怪
		boolean isOpen = worldIn.isAnyPlayerWithinRangeAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16);
		if (state.getValue(OPEN) != isOpen) worldIn.setBlockState(pos, state.withProperty(OPEN, isOpen), 1);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(OPEN) ? 15 : 0;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { OPEN });
	}

	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		if (!ESAPI.isDevelop) return;
		items.add(new ItemStack(this, 1, 0));
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.REDSTONE_LAMP);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(OPEN) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(OPEN, meta == 0 ? false : true);
	}

}
