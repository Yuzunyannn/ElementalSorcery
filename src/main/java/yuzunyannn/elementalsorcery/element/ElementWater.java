package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import yuzunyannn.elementalsorcery.element.explosion.EEWater;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;

public class ElementWater extends ElementCommon {

	public ElementWater() {
		super(0x6472f7, "water");
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;
		int power = estack.getPower();
		int n = (int) Math.max((1 - Math.min(1, power / 1000f)) * 20, 2);
		if (tick % n != 0) return estack;

		NBTTagCompound data = estack.getOrCreateTagCompound();
		int range = getStarFlowerRange(estack);
		int size = range * 2 + 1;
		int t = data.getInteger("t");
		int num = t % (size * size * 3);
		data.setInteger("t", num + 1);

		int x = num % size - range;
		int z = (num / size) % size - range;
		int y = -(num / (size * size)) % 3 - 1;

		BlockPos at = pos.add(x, y, z);
		if (!world.isAirBlock(at)) {
			IBlockState state = world.getBlockState(at);
			IBlockState newState = transform(state);
			if (newState != null) {
				world.setBlockState(at, newState);
				world.playEvent(2001, at, Block.getStateId(Blocks.WATER.getDefaultState()));
				estack.shrink(2);
			}
		}

		return estack;
	}

	public static IBlockState transform(IBlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.FIRE) return Blocks.AIR.getDefaultState();
		if (block == Blocks.DIRT) return Blocks.GRASS.getDefaultState();
		if (block == Blocks.MAGMA) return Blocks.COBBLESTONE.getDefaultState();
		if (block == Blocks.CAULDRON) return Blocks.CAULDRON.getDefaultState().withProperty(BlockCauldron.LEVEL, 3);
		if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
			if (state.getValue(BlockFluidBase.LEVEL) != 0) return Blocks.FLOWING_WATER.getDefaultState();
		}
		if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
			if (state.getValue(BlockFluidBase.LEVEL) == 0) return Blocks.OBSIDIAN.getDefaultState();
			else return Blocks.COBBLESTONE.getDefaultState();
		}
		return null;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEWater(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

}
