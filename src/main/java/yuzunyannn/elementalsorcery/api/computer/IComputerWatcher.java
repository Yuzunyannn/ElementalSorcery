package yuzunyannn.elementalsorcery.api.computer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IComputerWatcher {

	boolean isLeave();

	<T> T getDetectObject(Class<T> cls);

	<T> void setDetectObject(T obj);

	void sendMessageToClient(IMessage message);
}
