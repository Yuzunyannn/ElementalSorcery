package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.block.BlockElfSapling;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.CollectResult;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class MantraLush extends MantraCommon {

	public MantraLush() {
		this.setTranslationKey("lush");
		this.setColor(0x32CD32);
		this.setIcon("lush");
		this.setRarity(75);
		this.setDirectLaunchFragmentMantraLauncher(new ElementStack(ESInit.ELEMENTS.WOOD, 125, 50), 2, 0.0075, null);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		ElementStack stack = getElement(caster, ESInit.ELEMENTS.WOOD, 1, 10);
		if (stack.isEmpty()) return;

		float potent = caster.iWantBePotent(0.2f, false);
		doPotentAttackEffect(world, caster, target);
		magicAt(world, target.getPosition(), caster, stack.getPower() * (1 + potent));
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		((MantraDataCommon) data).markContinue(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		if (beforeGeneralStartTime(caster)) return;
		super.onSpellingEffect(world, data, caster);
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		if (beforeGeneralStartTime(caster)) return;
		MantraDataCommon mData = (MantraDataCommon) data;
		CollectResult cr = mData.tryCollect(caster, ESInit.ELEMENTS.WOOD, 1, 50, 125);
		mData.setProgress(cr.getStackCount(), 200);
	}

	@Override
	public boolean afterSpelling(World world, IMantraData mData, ICaster caster) {
		MantraDataCommon data = (MantraDataCommon) mData;
		ElementStack wood = data.get(ESInit.ELEMENTS.WOOD);
		if (wood.isEmpty()) return false;
		int tick = caster.iWantKnowCastTick();
		if (tick % 3 != 0) return true;
		BlockPos pos = caster.iWantCaster().getPosition();
		int size = data.get(SIZEI);
		float power = wood.getPower() * Math.max(0.2f, 1 - 0.05f * size);
		wood.shrink(size + 1);
		for (int x = -size; x <= size; x++) {
			if (x == -size || x == size)
				for (int z = -size; z <= size; z++) magicAt(world, pos.add(x, 0, z), caster, power);
			else {
				magicAt(world, pos.add(x, 0, -size), caster, power);
				magicAt(world, pos.add(x, 0, size), caster, power);
			}
		}
		data.set(SIZEI, size + 1);
		return true;
	}

	@SideOnly(Side.CLIENT)
	protected void magicEffectAt(World world, BlockPos pos, ICaster caster) {
		Random rand = RandomHelper.rand;
		for (int i = 0; i < rand.nextInt(3) + 1; i++) {
			Vec3d at = new Vec3d(pos).add(rand.nextDouble(), rand.nextDouble() * 0.25 + 0.1, rand.nextDouble());
			EffectElementMove effect = new EffectElementMove(world, at);
			effect.yAccelerate = 0.001;
			effect.setColor(getColor(null));
			Effect.addEffect(effect);
		}
	}

	/** 魔法在某个点释放 */
	protected void magicAt(World world, BlockPos pos, ICaster caster, float power) {
		// 寻找释放点
		boolean find = false;
		pos = pos.up(3);
		for (int y = 0; y >= -10; y--) {
			pos = pos.down();
			if (!world.isAirBlock(pos)) {
				find = true;
				break;
			}
		}
		if (!find) return;
		IBlockState state = world.getBlockState(pos);
		if (world.isRemote) {
			if (state.isFullBlock()) pos = pos.up();
			magicEffectAt(world, pos, caster);
			return;
		}
		magicToBlock(world, pos, state, power, caster.iWantCaster().asEntity());
	}

	public static void magicToBlock(World world, BlockPos pos, IBlockState state, float power,
			@Nullable Entity entity) {
		Block block = state.getBlock();
		Random rand = world.rand;
		float dP = MathHelper.sqrt(power / 8.0f);
		// 泥土变草
		if (block == Blocks.DIRT) {
			world.setBlockState(pos, Blocks.GRASS.getDefaultState());
			return;
		}
		// 雪消失
		if (block == Blocks.SNOW_LAYER) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return;
		}
		// 草方块长草
		if (block == Blocks.GRASS) {
			if (rand.nextFloat() < dP * 0.01) ((IGrowable) block).grow(world, rand, pos, state);
			return;
		}
		// 石头长苔
		if (block == Blocks.STONE) {
			if (rand.nextFloat() < dP * 0.06) world.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.getDefaultState());
			return;
		}
		// 苔石变土
		if (block == Blocks.MOSSY_COBBLESTONE || block == Blocks.SAND) {
			if (rand.nextFloat() < dP * 0.06) world.setBlockState(pos, Blocks.GRASS.getDefaultState());
			return;
		}
		// 可生长的植物
		if (state.isFullBlock()) return;
		// 生命之花
		if (block == ESInit.BLOCKS.LIFE_FLOWER) {
			world.scheduleUpdate(pos, state.getBlock(), 0);
			return;
		}
		if (block == ESInit.BLOCKS.ELF_SAPLING && power > 600) {
			if (rand.nextFloat() < dP * 0.05) {
				EntityPlayer player = null;
				if (entity instanceof EntityPlayer) player = (EntityPlayer) entity;
				((BlockElfSapling) block).superGrow(world, rand, pos, state, player, true);
				return;
			}
		}
		// 尝试生长
		if (block instanceof IGrowable) {
			IGrowable growable = (IGrowable) block;
			int n = (int) Math.floor(dP / 3);
			for (int i = 0; i < n; i++)
				if (growable.canGrow(world, pos, state, world.isRemote)) growable.grow(world, rand, pos, state);
		}
	}

}
