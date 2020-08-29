package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageEntitySync implements IMessage {

	public static interface IRecvData {
		void onRecv(NBTTagCompound data);
	}

	public NBTTagCompound nbt;

	public MessageEntitySync() {
		nbt = new NBTTagCompound();
	}

	public MessageEntitySync(Entity entity, NBTTagCompound data) {
		nbt = data;
		nbt.setInteger("eId", entity.getEntityId());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	static public class Handler implements IMessageHandler<MessageEntitySync, IMessage> {
		@Override
		public IMessage onMessage(MessageEntitySync message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) dealClient(message.nbt);
			else dealServer(message.nbt, ctx.getServerHandler().player);
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	static public void dealClient(NBTTagCompound data) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				int id = data.getInteger("eId");
				Entity entity = Minecraft.getMinecraft().world.getEntityByID(id);
				if (entity instanceof IRecvData) ((IRecvData) entity).onRecv(data);
			}
		});
	}

	static public void dealServer(NBTTagCompound data, EntityPlayerMP player) {
		final WorldServer world = player.getServerWorld();
		world.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				int id = data.getInteger("eId");
				Entity entity = world.getEntityByID(id);
				if (entity instanceof IRecvData) ((IRecvData) entity).onRecv(data);
			}
		});
	}
}
