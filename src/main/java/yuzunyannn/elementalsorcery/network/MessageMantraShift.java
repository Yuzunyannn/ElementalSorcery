package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;

public class MessageMantraShift implements IMessage {
	public short to;

	public MessageMantraShift() {
	}

	public MessageMantraShift(int to) {
		this.to = (short) to;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		to = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(to);
	}

	static public class Handler implements IMessageHandler<MessageMantraShift, IMessage> {
		@Override
		public IMessage onMessage(MessageMantraShift message, MessageContext ctx) {
			if (ctx.side != Side.SERVER) return null;
			EntityPlayerMP player = ctx.getServerHandler().player;
			ItemStack grimoire = player.getHeldItemMainhand();
			Grimoire.shiftMantra(grimoire, message.to);
			return null;
		}
	}
}
