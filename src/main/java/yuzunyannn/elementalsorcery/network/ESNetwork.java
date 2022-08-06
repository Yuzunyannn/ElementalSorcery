package yuzunyannn.elementalsorcery.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.ESAPI;

public class ESNetwork {

	public final static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE
			.newSimpleChannel(ESAPI.MODID);
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

		registerMessage(MessageElementExplosion.Handler.class, MessageElementExplosion.class, Side.CLIENT);
		registerMessage(MessageBlockDisintegrate.Handler.class, MessageBlockDisintegrate.class, Side.CLIENT);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
	}

	
	
	
	
	public static void sendMessage(IMessage msg, World world, Vec3d vec) {
		sendMessage(msg, world.provider.getDimension(), vec);
	}

	public static void sendMessage(IMessage msg, World world, BlockPos pos) {
		sendMessage(msg, world.provider.getDimension(),
				new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
	}

	public static void sendMessage(IMessage msg, int dim, Vec3d vec) {
		TargetPoint point = new TargetPoint(dim, vec.x, vec.y, vec.z, 64);
		ESNetwork.instance.sendToAllAround(msg, point);
	}

}
