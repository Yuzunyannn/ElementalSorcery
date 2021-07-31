package yuzunyannn.elementalsorcery.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class ESNetwork {

	public final static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE
			.newSimpleChannel(ElementalSorcery.MODID);
	private static int nextID = 0;

	public static void registerAll() {
		registerMessage(MessageSpellbook.Handler.class, MessageSpellbook.class, Side.CLIENT);
		registerMessage(MessageEffect.Handler.class, MessageEffect.class, Side.CLIENT);
		registerMessage(MessageEntitySync.Handler.class, MessageEntitySync.class, Side.CLIENT);

		registerMessage(MessageMantraShift.Handler.class, MessageMantraShift.class, Side.SERVER);
		registerMessage(MessageElfTreeElevator.Handler.class, MessageElfTreeElevator.class, Side.SERVER);

		registerMessage(MessageGetBuilingInfo.Handler.class, MessageGetBuilingInfo.class, Side.CLIENT);
		registerMessage(MessageGetBuilingInfo.Handler.class, MessageGetBuilingInfo.class, Side.SERVER);

		registerMessage(MessageSyncContainer.Handler.class, MessageSyncContainer.class, Side.CLIENT);
		registerMessage(MessageSyncContainer.Handler.class, MessageSyncContainer.class, Side.SERVER);

		registerMessage(MessageSyncConfig.Handler.class, MessageSyncConfig.class, Side.CLIENT);

		registerMessage(MessagePocketWatch.Handler.class, MessagePocketWatch.class, Side.CLIENT);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
	}
}
