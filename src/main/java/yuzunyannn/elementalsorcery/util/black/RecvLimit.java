package yuzunyannn.elementalsorcery.util.black;

import net.minecraft.entity.player.EntityPlayer;

public final class RecvLimit {

	private long lastNano;
	private double accumulate;
	private int nPerSec;
	private double limit;

	public RecvLimit(int nPerSec, double limit) {
		this.lastNano = System.nanoTime();
		this.nPerSec = nPerSec;
		this.limit = limit;
	}

	public boolean peg(EntityPlayer player) {
		return peg();
	}

	public boolean peg() {
		long currNano = System.nanoTime();
		long dNano = currNano - lastNano;
		lastNano = currNano;

		double dSec = (dNano / (double) (1000 * 1000 * 1000));
		accumulate = Math.max(accumulate + 1 - dSec * nPerSec, 0);

		return accumulate >= limit;
	}

}
