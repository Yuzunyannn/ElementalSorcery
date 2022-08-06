package yuzunyannn.elementalsorcery.network;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;

public class MessageSyncContainer implements IMessage {

	public static interface IContainerNetwork {

		void recvData(NBTTagCompound nbt, Side side);

		@SideOnly(Side.CLIENT)
		default void sendToServer(NBTTagCompound nbt) {
			if (nbt == null) return;
			MessageSyncContainer message = new MessageSyncContainer();
			message.nbt = nbt;
			message.nbt.setInteger("_gui", ((Container) this).windowId);
			ESNetwork.instance.sendToServer(message);
		}

		default void sendToClient(NBTTagCompound nbt, List<IContainerListener> listeners) {
			if (nbt == null) return;
			MessageSyncContainer message = new MessageSyncContainer();
			message.nbt = nbt;
			message.nbt.setInteger("_gui", ((Container) this).windowId);
			for (IContainerListener listener : listeners) {
				if (listener instanceof EntityPlayerMP) ESNetwork.instance.sendTo(message, (EntityPlayerMP) listener);
			}
		}

		default void sendToClient(NBTTagCompound nbt, IContainerListener listener) {
			if (nbt == null || listener == null) return;
			MessageSyncContainer message = new MessageSyncContainer();
			message.nbt = nbt;
			message.nbt.setInteger("_gui", ((Container) this).windowId);
			if (listener instanceof EntityPlayerMP) ESNetwork.instance.sendTo(message, (EntityPlayerMP) listener);
		}

		default void sendToClient(NBTTagCompound nbt, EntityPlayer player) {
			if (player instanceof EntityPlayerMP) sendToClient(nbt, (IContainerListener) player);
		}
	}

	public NBTTagCompound nbt = new NBTTagCompound();

	public MessageSyncContainer() {

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	static public class Handler implements IMessageHandler<MessageSyncContainer, IMessage> {

		@Override
		public IMessage onMessage(MessageSyncContainer message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				Minecraft.getMinecraft().addScheduledTask(new Runnable() {
					@Override
					public void run() {
						updateToClient(message);
					}
				});
			} else {
				ctx.getServerHandler().player.world.getMinecraftServer().addScheduledTask(new Runnable() {

					@Override
					public void run() {
						requestFromClient(message, ctx.getServerHandler().player);
					}
				});

			}
			return null;
		}

		@SideOnly(Side.CLIENT)
		public void updateToClient(MessageSyncContainer msg) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			Container gui = player.openContainer;
			if (gui != null && gui instanceof IContainerNetwork && gui.windowId == msg.nbt.getInteger("_gui")) {
				msg.nbt.removeTag("_gui");
				((IContainerNetwork) gui).recvData(msg.nbt, Side.CLIENT);
			} else ESAPI.logger.warn("gui不吻合");
		}

		public void requestFromClient(MessageSyncContainer msg, EntityPlayerMP player) {
			Container gui = player.openContainer;
			if (gui != null && gui instanceof IContainerNetwork && gui.windowId == msg.nbt.getInteger("_gui")) {
				msg.nbt.removeTag("_gui");
				((IContainerNetwork) gui).recvData(msg.nbt, Side.SERVER);
			} else ESAPI.logger.warn("gui不吻合");
		}

	}
}
