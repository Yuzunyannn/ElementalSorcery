package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleMagicTorch;

public class BlockMagicTorch extends BlockTorch {

	public static final PropertyBool LIT = PropertyBool.create("lit");

	public BlockMagicTorch() {
		this.setSoundType(SoundType.WOOD);
		this.setTranslationKey("magicTorch");
		this.setDefaultState(this.getDefaultState().withProperty(LIT, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, LIT });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = super.getMetaFromState(state);
		return i | (state.getValue(LIT) ? 8 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = super.getStateFromMeta(meta & 7);
		if ((meta & 8) != 0) state = state.withProperty(LIT, true);
		return state;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(LIT)) return 7;
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.getValue(LIT)) {
			double d0 = (double) pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d1 = (double) pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d2 = (double) pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			EnumFacing enumfacing = (EnumFacing) stateIn.getValue(FACING);

			if (enumfacing.getAxis().isHorizontal()) {
				EnumFacing enumfacing1 = enumfacing.getOpposite();
				d0 += 0.27D * (double) enumfacing1.getXOffset();
				d1 += 0.22D;
				d2 += 0.27D * (double) enumfacing1.getZOffset();
			}
			Particle effect = new ParticleMagicTorch(worldIn, d0, d1, d2, 1.0f, 0.7f, 0.25f, 0.9f);
			Minecraft.getMinecraft().effectRenderer.addEffect(effect);
		}
	}

}
