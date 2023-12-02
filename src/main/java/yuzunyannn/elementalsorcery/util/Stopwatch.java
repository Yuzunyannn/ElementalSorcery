package yuzunyannn.elementalsorcery.util;

import yuzunyannn.elementalsorcery.api.ESAPI;

public class Stopwatch {

	protected long nano;
	protected long cumulative;
	protected boolean inRecord;

	public void clear() {
		if (inRecord) stop();
		cumulative = 0;
	}

	public void start() {
		if (inRecord && ESAPI.isDevelop) throw new RuntimeException("repeated restarts");
		inRecord = true;
		nano = System.nanoTime();
	}

	public void stop() {
		if (!inRecord && ESAPI.isDevelop) throw new RuntimeException("repeated end");
		inRecord = false;
		cumulative = cumulative + (System.nanoTime() - nano);
	}

	public long getCumulative() {
		if (inRecord) return cumulative + System.nanoTime() - nano;
		return cumulative;
	}

	public boolean msLessThan(double ms) {
		return getCumulative() < (ms * 1000 * 1000);
	}

}
