package yuzunyannn.elementalsorcery.potion;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.EntityHelper;

public class PotionVerdantWalker extends PotionCommon {

	public PotionVerdantWalker() {
		super(false, 0x3ba400, "verdantWalker");
		iconIndex = 6;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 5 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase owner, int amplifier) {
		if (owner.world.isRemote) return;
		double v = PotionWindWalker.getEntitySpeed(owner);
		if (v < 0.12f) return;

		World world = owner.world;
		BlockPos pos = new BlockPos(owner.posX, owner.posY - 0.1f, owner.posZ);
		IBlockState state = world.getBlockState(pos);
		float point = getVerdantPoint(world, pos, state);
		if (point == 0) return;

		float hp = owner.getHealth();

		if (point > 0) {
			float maxHp = owner.getMaxHealth();
			float healPoint = point * (1 + (float) v * 0.25f) * (amplifier + 1);
			owner.heal(healPoint);
			if (owner instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) owner;
				player.getFoodStats().addStats(Math.round(healPoint * 2), 0.25f);
			}
			if (hp >= maxHp) {
				Random rand = owner.getRNG();
				if (rand.nextInt(16) == 0) {
					if (state.getBlock() instanceof IGrowable) {
						IGrowable growable = (IGrowable) state.getBlock();
						if (growable.canGrow(world, pos, state, false)) growable.grow(world, rand, pos, state);
					}
				}
			}
			return;
		}

		boolean isCreative = !owner.isNonBoss() || EntityHelper.isCreative(owner);

		float consume = -point * (4 - (amplifier * 0.1f));
		if (!isCreative) {
			if (hp - consume < 1) return;
			owner.setHealth(hp - consume);
		}

		IBlockState newState = getTransfer(state);
		if (newState == null) return;
		world.setBlockState(pos, newState);
	}

	public static float getVerdantPoint(IBlockAccess world, BlockPos pos, IBlockState state) {
		if (world.isAirBlock(pos)) return 0;
		Block block = state.getBlock();
		if (block == Blocks.GRASS) {
			IBlockState brush = world.getBlockState(pos.up());
			if (brush.getBlock() instanceof BlockBush) return 0.125f;
			return 0.075f;
		} else if (block instanceof BlockLeaves) return 0.04f;
		else if (block == Blocks.SOUL_SAND) return 0.15f;
		else if (block == Blocks.GRASS_PATH) return -0.01f;
		else if (block == Blocks.DIRT) return -0.05f;
		else if (block == Blocks.STONE) return -0.125f;
		else if (block == Blocks.COBBLESTONE) return -0.1f;
		else if (block == Blocks.MOSSY_COBBLESTONE) return -0.1f;
		else if (block == Blocks.NETHERRACK) return -0.1f;
		else if (block == Blocks.SAND) return -0.075f;
		else if (block == Blocks.GRAVEL) return -0.075f;
		else if (block == Blocks.SANDSTONE) return -0.125f;
		return 0;
	}

	public static IBlockState getTransfer(IBlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.GRASS_PATH) return Blocks.GRASS.getDefaultState();
		else if (block == Blocks.DIRT) return Blocks.GRASS.getDefaultState();
		else if (block == Blocks.STONE) return Blocks.MOSSY_COBBLESTONE.getDefaultState();
		else if (block == Blocks.COBBLESTONE) return Blocks.MOSSY_COBBLESTONE.getDefaultState();
		else if (block == Blocks.MOSSY_COBBLESTONE)
			return Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
		else if (block == Blocks.COBBLESTONE) return Blocks.MOSSY_COBBLESTONE.getDefaultState();
		else if (block == Blocks.SAND || block == Blocks.SANDSTONE || block == Blocks.GRAVEL
				|| block == Blocks.NETHERRACK)
			return Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
		return null;
	}

}
