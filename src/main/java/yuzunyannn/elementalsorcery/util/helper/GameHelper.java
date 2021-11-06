package yuzunyannn.elementalsorcery.util.helper;

import net.minecraft.client.Minecraft;

public class GameHelper {

	/** 客户端时才会回调的函数，不会投出任何异常 */
	public static void clientRun(Runnable run) {
		try {
			Minecraft.getMinecraft();
			run.run();
		} catch (Throwable e) {}
	}

}
