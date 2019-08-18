package yuzunyannn.elementalsorcery.tile.md;

public class TileMDMagicGen extends TileMDBase {

	@Override
	public int getMaxCapacity() {
		return 5000;
	}

	@Override
	protected int getOverflow() {
		return 0;
	}

	@Override
	protected int getMaxSendCountOnce() {
		return 50;
	}

}
