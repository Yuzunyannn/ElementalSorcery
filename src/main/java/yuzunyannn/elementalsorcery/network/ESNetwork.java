package yuzunyannn.elementalsorcery.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class ESNetwork {
	public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(ElementalSorcery.MODID);
	private static int nextID = 0;

	public static void registerAll() {
		registerMessage(MessageSpellbook.Handler.class, MessageSpellbook.class, Side.CLIENT);
		
		registerMessage(MessageSyncItemStack.Handler.class, MessageSyncItemStack.class, Side.CLIENT);
		
		registerMessage(MessageGetBuilingInfo.Handler.class, MessageGetBuilingInfo.class, Side.CLIENT);
		registerMessage(MessageGetBuilingInfo.Handler.class, MessageGetBuilingInfo.class, Side.SERVER);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
	}
}
