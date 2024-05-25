package yuzunyannn.elementalsorcery.computer;

import net.minecraft.entity.player.EntityPlayerMP;
import yuzunyannn.elementalsorcery.api.computer.IComputerWatcher;
import yuzunyannn.elementalsorcery.api.util.detecter.SyncWatcher;
import yuzunyannn.elementalsorcery.container.ContainerComputer;

public class WatcherConatiner extends SyncWatcher implements IComputerWatcher {

	public final EntityPlayerMP player;
	public final ContainerComputer container;

	public WatcherConatiner(ContainerComputer container) {
		this.player = (EntityPlayerMP) container.player;
		this.container = container;
	}

	@Override
	public boolean isLeave() {
		return this.container.isClosed;
	}

//	@Override
//	public void sendMessageToClient(IMessage msg) {
//		ESNetwork.instance.sendTo(msg, player);
//	}

}
