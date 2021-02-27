package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;

public class MessageElfTreeElevator implements IMessage {
	public short to;
	public BlockPos pos;

	public MessageElfTreeElevator() {
	}

	public MessageElfTreeElevator(BlockPos pos, int to) {
		this.to = (short) to;
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		to = buf.readShort();
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(to);
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

	static public class Handler implements IMessageHandler<MessageElfTreeElevator, IMessage> {
		@Override
		public IMessage onMessage(MessageElfTreeElevator message, MessageContext ctx) {
			if (ctx.side != Side.SERVER) return null;

			final EntityPlayerMP player = ctx.getServerHandler().player;
			final WorldServer world = player.getServerWorld();
			world.addScheduledTask(() -> {
				TileElfTreeCore.moveEntity(message.pos, player, message.to);
			});

			return null;
		}
	}
}
