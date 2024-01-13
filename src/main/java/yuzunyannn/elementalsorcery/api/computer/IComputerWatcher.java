package yuzunyannn.elementalsorcery.api.computer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import yuzunyannn.elementalsorcery.api.util.ISyncWatcher;

public interface IComputerWatcher extends ISyncWatcher {

	void sendMessageToClient(IMessage message);

}
