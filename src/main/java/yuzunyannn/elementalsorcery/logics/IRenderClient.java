package yuzunyannn.elementalsorcery.logics;

public interface IRenderClient {

	public static final int SUCCESS = 1;
	public static final int END = 0;
	
	int onRender(float partialTicks);
}
