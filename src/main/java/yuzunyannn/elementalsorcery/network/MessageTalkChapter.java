package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerElfTalk;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;

public class MessageTalkChapter implements IMessage {

	public NBTTagCompound nbt = new NBTTagCompound();

	public MessageTalkChapter() {

	}

	public MessageTalkChapter(TalkChapter chapter, EntityElfBase elf, Container gui) {
		nbt = chapter.serializeNBTToSend();
		if (elf != null) nbt.setInteger("elfId", elf.getEntityId());
		nbt.setInteger("gui", gui.windowId);
	}

	public MessageTalkChapter(int to, Container gui) {
		nbt.setInteger("to", to);
		nbt.setInteger("gui", gui.windowId);
	}

	@SideOnly(Side.CLIENT)
	public MessageTalkChapter(int to, int selectAt, Container gui) {
		nbt.setInteger("to", to);
		nbt.setInteger("select", selectAt);
		nbt.setInteger("gui", gui.windowId);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	static public class Handler implements IMessageHandler<MessageTalkChapter, IMessage> {

		@Override
		public IMessage onMessage(MessageTalkChapter message, MessageContext ctx) {
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
		public void updateToClient(MessageTalkChapter msg) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			// 同步charpter类型情况
			if (msg.nbt.hasKey("scenes")) {
				Container gui = player.openContainer;
				if (gui != null && gui instanceof ContainerElfTalk && gui.windowId == msg.nbt.getInteger("gui")) {
					((ContainerElfTalk) gui).setChapter(new TalkChapter().deserializeNBTFromSend(msg.nbt));
					Entity elf = player.world.getEntityByID(msg.nbt.getInteger("elfId"));
					if (elf instanceof EntityElfBase) ((ContainerElfTalk) gui).elf = (EntityElfBase) elf;
				} else {
					ElementalSorcery.logger.warn("对话chapter同步时，gui不吻合");
				}
			}
			// 转跳情况
			if (msg.nbt.hasKey("to")) {
				Container gui = player.openContainer;
				if (gui != null && gui instanceof ContainerElfTalk && gui.windowId == msg.nbt.getInteger("gui")) {
					((ContainerElfTalk) gui).toOrPassIndex(msg.nbt.getInteger("to"), 0);
				} else {
					ElementalSorcery.logger.warn("对话chapter同步转跳时，gui不吻合");
				}
			}
		}

		public void requestFromClient(MessageTalkChapter msg, EntityPlayerMP player) {
			if (player.openContainer.windowId == msg.nbt.getInteger("gui")) {
				int ret = ((ContainerElfTalk) player.openContainer).toOrPassIndex(msg.nbt.getInteger("to"),
						msg.nbt.getInteger("select"));
				if (ret >= 0) {
					// 有内容，返回去告诉客户端要设置
					ESNetwork.instance.sendTo(new MessageTalkChapter(ret, player.openContainer), player);
				}
			} else {
				ElementalSorcery.logger.warn("对话chapter同步转跳时，gui不吻合");
			}
		}

	}
}
