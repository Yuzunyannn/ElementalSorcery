package yuzunyannn.elementalsorcery.block.container;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElementMove;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;

public class BlockElfTreeCore extends BlockContainerNormal {

	public BlockElfTreeCore() {
		super(Material.GLASS, "elfTreeCore", 20F);
		this.setLightLevel(0.5f);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileElfTreeCore();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		// 不在创造模式下显示
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		// 不能掉落
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		Vec3d at = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double x = rand.nextFloat() * 0.5 - 0.25;
		double z = rand.nextFloat() * 0.5 - 0.25;
		EffectElementMove effect = new EffectElementMove(worldIn, at.addVector(x, 0, z));
		effect.g = 0.0025;
		effect.setColor(0x0d9d17);
		Effect.addEffect(effect);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

}
