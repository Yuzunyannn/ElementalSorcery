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

	public final static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(ESAPI.MODID);

	public static void registerAll() {
		regMsg(0, MessageSpellbook.Handler.class, MessageSpellbook.class, Side.CLIENT);
		regMsg(1, MessageEffect.Handler.class, MessageEffect.class, Side.CLIENT);
		regMsg(2, MessageEntitySync.Handler.class, MessageEntitySync.class, Side.CLIENT);
		regMsg(3, MessageMantraShift.Handler.class, MessageMantraShift.class, Side.SERVER);
		regMsg(4, MessageElfTreeElevator.Handler.class, MessageElfTreeElevator.class, Side.SERVER);
		
		regMsg(5, MessageGetBuilingInfo.Handler.class, MessageGetBuilingInfo.class, Side.CLIENT);
		regMsg(6, MessageGetBuilingInfo.Handler.class, MessageGetBuilingInfo.class, Side.SERVER);
		
		regMsg(7, MessageSyncContainer.Handler.class, MessageSyncContainer.class, Side.CLIENT);
		regMsg(8, MessageSyncContainer.Handler.class, MessageSyncContainer.class, Side.SERVER);
		
		regMsg(9, MessageSyncConfig.Handler.class, MessageSyncConfig.class, Side.CLIENT);
		regMsg(10, MessagePocketWatch.Handler.class, MessagePocketWatch.class, Side.CLIENT);
		regMsg(11, MessageElementExplosion.Handler.class, MessageElementExplosion.class, Side.CLIENT);
		regMsg(12, MessageBlockDisintegrate.Handler.class, MessageBlockDisintegrate.class, Side.CLIENT);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void regMsg(int id,
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, id, side);
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
