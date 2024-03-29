package yuzunyannn.elementalsorcery.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbook;

public class MessageSpellbook implements IMessage {
	public NBTTagCompound nbt = new NBTTagCompound();

	public MessageSpellbook() {

	}

	public MessageSpellbook setSpellFinish(EntityLivingBase playerIn) {
		nbt.setUniqueId("uuid", playerIn.getUniqueID());
		nbt.setByte("flags", (byte) 0);
		return this;
	}

	public MessageSpellbook setSpellStart(EntityLivingBase playerIn, Spellbook book) {
		nbt.setUniqueId("uuid", playerIn.getUniqueID());
		nbt.setByte("flags", (byte) 1);
		book.getInventory().saveState(nbt);
		return this;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	static public class Handler implements IMessageHandler<MessageSpellbook, IMessage> {
		@Override
		public IMessage onMessage(MessageSpellbook message, MessageContext ctx) {
			if (ctx.side != Side.CLIENT) return null;
			UUID uuid = message.nbt.getUniqueId("uuid");
			byte flags = message.nbt.getByte("flags");
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
					if (player == null) {
						ESAPI.logger.warn("MessageSpellbook的包接受时候出现问题：clinet寻找不到对应的玩家！难道是僵尸？！");
						return;
					}
					ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
					if (!stack.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null))
						stack = player.getHeldItem(EnumHand.OFF_HAND);
					if (!stack.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null)) return;

					if (flags == 0)
						ItemSpellbook.renderFinish(stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null));
					else if (flags == 1) {
						Spellbook book = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
						book.getInventory().loadState(message.nbt);
					}
				}
			});
			return null;
		}
	}
}
