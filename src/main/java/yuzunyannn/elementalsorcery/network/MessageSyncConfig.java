package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.config.ESConfig;

public class MessageSyncConfig implements IMessage {

	public NBTTagCompound nbt = new NBTTagCompound();

	public MessageSyncConfig() {

	}

	public MessageSyncConfig(NBTTagCompound config) {
		this.nbt = config;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	static public class Handler implements IMessageHandler<MessageSyncConfig, IMessage> {

		@Override
		public IMessage onMessage(MessageSyncConfig message, MessageContext ctx) {
			if (ctx.side != Side.CLIENT) return null;
			updateToClient(message);

			return null;
		}

		@SideOnly(Side.CLIENT)
		public void updateToClient(MessageSyncConfig msg) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ESConfig.sync(msg.nbt);
			});
		}

	}
}
