package yuzunyannn.elementalsorcery.event;

public interface ITickTask {

	public static final int SUCCESS = 1;
	public static final int END = 0;

	int onTick();
}
