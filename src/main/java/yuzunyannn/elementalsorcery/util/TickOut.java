package yuzunyannn.elementalsorcery.util;

public class TickOut {
	private int restTick;

	public TickOut(int restTick) {
		this.restTick = restTick;
	}

	public void reset(int restTick) {
		this.restTick = restTick;
	}

	/**
	 * 减少1tick
	 * 
	 * @return 返回true标识还在倒计时
	 */
	public boolean tick() {
		if (this.restTick == 0)
			return false;
		this.restTick--;
		return true;
	}
}
