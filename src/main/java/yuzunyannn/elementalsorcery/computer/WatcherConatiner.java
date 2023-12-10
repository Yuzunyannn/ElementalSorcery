package yuzunyannn.elementalsorcery.computer;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import yuzunyannn.elementalsorcery.container.ContainerComputer;
import yuzunyannn.elementalsorcery.network.ESNetwork;

public class WatcherConatiner extends WatcherCommon {

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

	@Override
	public void sendMessageToClient(IMessage msg) {
		ESNetwork.instance.sendTo(msg, player);
	}

}
