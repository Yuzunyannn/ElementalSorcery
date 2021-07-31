package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ts.PocketWatchClient;

public class MessagePocketWatch implements IMessage {

	public int worldId;
	public int tick;

	public MessagePocketWatch() {

	}

	public MessagePocketWatch(World world, int tick) {
		this.worldId = world.provider.getDimension();
		this.tick = tick;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.worldId = buf.readInt();
		this.tick = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(worldId);
		buf.writeInt(tick);
	}

	static public class Handler implements IMessageHandler<MessagePocketWatch, IMessage> {

		@Override
		public IMessage onMessage(MessagePocketWatch message, MessageContext ctx) {
			if (ctx.side != Side.CLIENT) return null;
			updateToClient(message);
			return null;
		}

		@SideOnly(Side.CLIENT)
		public void updateToClient(MessagePocketWatch msg) {
			final int worldId = msg.worldId;
			final int tick = msg.tick;
			PocketWatchClient.mc.addScheduledTask(() -> {
				if (PocketWatchClient.mc.world.provider.getDimension() != worldId) return;
				PocketWatchClient.stopWorld(tick, null);
			});
		}

	}
}
