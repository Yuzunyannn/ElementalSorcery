package yuzunyan.elementalsorcery.event;

public interface ITickClient {

	public static final int SUCCESS = 1;
	public static final int END = 0;

	int onTick();
}
