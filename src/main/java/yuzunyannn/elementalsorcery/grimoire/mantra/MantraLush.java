package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.block.BlockElfSapling;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectElementMove;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraLush extends MantraCommon {

	protected static class Data extends MantraDataCommon {
		ElementStack power = new ElementStack(ESInitInstance.ELEMENTS.WOOD, 0);

		int size = 0;
	}

	public MantraLush() {
		this.setUnlocalizedName("lush");
		this.setRarity(75);
	}

	@Override
	public Element getMagicCircle() {
		return ESInitInstance.ELEMENTS.WOOD;
	}

	@Override
	public int getRenderColor() {
		return 0x32CD32;
	}

	@Override
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_LUSH;
	}

	@Override
	public IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return new Data();
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		dataEffect.markContinue(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		if (super.onSpellingEffect(world, mData, caster)) return true;
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return false;
		Data data = (Data) mData;
		// 进度条特效
		data.setProgress(data.power.getCount() / 200.0f, this.getRenderColor(), world, caster);
		return false;
	}

	@Override
	public void onSpelling(World world, IMantraData mData, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;
		Data data = (Data) mData;
		if (data.power.getCount() >= 200) {
			super.onSpelling(world, mData, caster);
			return;
		}
		// 每tick两点消耗
		ElementStack need = new ElementStack(ESInitInstance.ELEMENTS.WOOD, 1, 50);
		ElementStack estack = caster.iWantSomeElement(need, true);
		if (estack.isEmpty()) return;
		data.power.grow(estack);
		super.onSpelling(world, mData, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData mData, ICaster caster) {
	}

	@Override
	public boolean afterSpelling(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		if (data.power.isEmpty()) return false;
		int tick = caster.iWantKnowCastTick();
		if (tick % 3 != 0) return true;
		BlockPos pos = caster.iWantCaster().getPosition();
		float power = data.power.getPower() * Math.max(0.2f, 1 - 0.05f * data.size);
		data.power.shrink(data.size + 1);
		for (int x = -data.size; x <= data.size; x++) {
			if (x == -data.size || x == data.size)
				for (int z = -data.size; z <= data.size; z++) magicAt(world, pos.add(x, 0, z), caster, power);
			else {
				magicAt(world, pos.add(x, 0, -data.size), caster, power);
				magicAt(world, pos.add(x, 0, data.size), caster, power);
			}
		}
		data.size++;
		return true;
	}

	@SideOnly(Side.CLIENT)
	protected void magicEffectAt(World world, BlockPos pos, ICaster caster) {
		Random rand = RandomHelper.rand;
		for (int i = 0; i < rand.nextInt(3) + 1; i++) {
			Vec3d at = new Vec3d(pos).addVector(rand.nextDouble(), rand.nextDouble() * 0.25 + 0.1, rand.nextDouble());
			EffectElementMove effect = new EffectElementMove(world, at);
			effect.g = -0.001;
			effect.setColor(this.getRenderColor());
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
		this.magicToBlock(world, pos, state, power);
	}

	protected void magicToBlock(World world, BlockPos pos, IBlockState state, float power) {
		Block block = state.getBlock();
		Random rand = world.rand;
		float dP = MathHelper.sqrt(power / 8.0f);
		// 泥土变草
		if (block == Blocks.DIRT) {
			world.setBlockState(pos, Blocks.GRASS.getDefaultState());
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
		if (block == ESInitInstance.BLOCKS.LIFE_FLOWER) {
			world.scheduleUpdate(pos, state.getBlock(), 0);
			return;
		}
		if (block == ESInitInstance.BLOCKS.ELF_SAPLING && power > 600) {
			if (rand.nextFloat() < dP * 0.05) {
				((BlockElfSapling) block).superGrow(world, rand, pos, state, true);
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
