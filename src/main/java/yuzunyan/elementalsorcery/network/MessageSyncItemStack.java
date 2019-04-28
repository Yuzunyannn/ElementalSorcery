package yuzunyan.elementalsorcery.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncItemStack implements IMessage {

	public NBTTagCompound nbt = new NBTTagCompound();

	public MessageSyncItemStack() {

	}

	public MessageSyncItemStack(EntityPlayer player, ItemStack stack) {
		nbt.setInteger("loc", player.inventory.currentItem);
		nbt.setTag("stack", stack.serializeNBT());
		nbt.setUniqueId("uuid", player.getUniqueID());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	static public class Handler implements IMessageHandler<MessageSyncItemStack, IMessage> {
		@Override
		public IMessage onMessage(MessageSyncItemStack message, MessageContext ctx) {
			if (ctx.side != Side.CLIENT)
				return null;
			UUID uuid = message.nbt.getUniqueId("uuid");
			ItemStack stack = new ItemStack(message.nbt.getCompoundTag("stack"));
			int i = message.nbt.getInteger("loc");
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
					if (player == null)
						return;
					player.inventory.setInventorySlotContents(i, stack);
				}
			});
			return null;
		}
	}
}
