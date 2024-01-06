package yuzunyannn.elementalsorcery.api.computer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IComputerWatcher extends ISyncWatcher {

	void sendMessageToClient(IMessage message);

}
