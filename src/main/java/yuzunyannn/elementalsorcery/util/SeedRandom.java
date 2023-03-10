package yuzunyannn.elementalsorcery.util;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

@SuppressWarnings("serial")
public class SeedRandom extends Random {

	public static long nextSeed(long seed, long salt) {
		return seed * 21701 + salt;
	}

	protected long seed;

	public SeedRandom(long seed) {
		this.seed = seed;
	}

	public SeedRandom(BlockPos pos) {
		this(pos.getX() * pos.getX() + pos.getY() * pos.getZ());
	}

	public long getSeed() {
		return seed;
	}

	@Override
	public void setSeed(long seed) {
		this.seed = seed;
	}

	@Override
	protected int next(int bits) {
		seed = nextSeed(seed, 49937);
		return ((int) seed) >>> (32 - bits);
	}
}
