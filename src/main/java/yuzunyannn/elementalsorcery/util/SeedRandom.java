package yuzunyannn.elementalsorcery.util;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

@SuppressWarnings("serial")
public class SeedRandom extends Random {

	protected int seed;

	public SeedRandom(int seed) {
		this.seed = seed;
	}

	public SeedRandom(BlockPos pos) {
		this(pos.getX() * pos.getX() + pos.getY() * pos.getZ());
	}

	public int getSeed() {
		return seed;
	}

	public SeedRandom setSeed(int seed) {
		this.seed = seed;
		return this;
	}

	@Override
	protected int next(int bits) {
		seed = seed * 21701 + 49937;
		return ((int) seed) >>> (32 - bits);
	}
}
