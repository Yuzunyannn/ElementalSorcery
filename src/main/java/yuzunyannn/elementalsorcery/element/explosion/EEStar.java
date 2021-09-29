package yuzunyannn.elementalsorcery.element.explosion;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.block.BlockAStone;
import yuzunyannn.elementalsorcery.block.BlockAStone.EnumType;
import yuzunyannn.elementalsorcery.block.BlockStarFlower;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;

public class EEStar extends ElementExplosion {

	public EEStar(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
	}

	@Override
	protected float getExplosionResistance(BlockPos pos, IBlockState state) {
		return super.getExplosionResistance(pos, state) * 0.05f;
	}

	@Override
	protected void doExplosionBlockAt(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() == Material.AIR) return;
		if (world.isRemote) spawnEffectFromBlock(pos);
		else {
			IBlockState newState = transfer(state, rand, eStack.getPower() * MathHelper.sqrt(eStack.getCount() / 10f));
			if (newState != null) world.setBlockState(pos, newState);
		}
	}

	@Override
	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {
		if (entity instanceof EntityLivingBase) {
			BlockStarFlower.bene((EntityLivingBase) entity, new BlockPos(position));
		}
	}

	static public IBlockState transfer(IBlockState state, Random rand, float power) {

		Block block = state.getBlock();

		if (block == Blocks.COBBLESTONE) {
			if (rand.nextFloat() < power / 100) return Blocks.STONE.getDefaultState();
		}
		if (block == ESInit.BLOCKS.ASTONE && state.getValue(BlockAStone.VARIANT) == EnumType.FRAGMENTED) {
			if (rand.nextFloat() < power / 600) return ESInit.BLOCKS.ASTONE.getDefaultState();
		}
		if (block == Blocks.SAND) {
			if (rand.nextFloat() < power / 1000) return ESInit.BLOCKS.STAR_SAND.getDefaultState();
		}
		if (block == ESInit.BLOCKS.KYANITE_ORE) {
			if (rand.nextFloat() < power / 4000) return ESInit.BLOCKS.KYANITE_BLOCK.getDefaultState();
		}
		if (block == Blocks.GOLD_ORE) {
			if (rand.nextFloat() < power / 2500) return Blocks.GOLD_BLOCK.getDefaultState();
		}
		if (block == Blocks.IRON_ORE) {
			if (rand.nextFloat() < power / 3000) return Blocks.IRON_BLOCK.getDefaultState();
		}
		if (block == Blocks.RED_FLOWER) {
			if (rand.nextFloat() < power / 50) return state.cycleProperty(Blocks.RED_FLOWER.getTypeProperty());
		}
		return null;
	}
}
