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
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;

public class MessageGetBuilingInfo implements IMessage {

	public NBTTagCompound nbt = new NBTTagCompound();

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	public MessageGetBuilingInfo() {

	}

	/** 服务端传递的建筑信息 */
	public MessageGetBuilingInfo(String keyName, Building building) {
		nbt = building.serializeNBT();
		nbt.setString("key", keyName);
	}

	@SideOnly(Side.CLIENT)
	public MessageGetBuilingInfo(String keyName) {
		nbt.setString("key", keyName);
	}

	static public class Handler implements IMessageHandler<MessageGetBuilingInfo, IMessage> {

		@Override
		public IMessage onMessage(MessageGetBuilingInfo message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				Minecraft.getMinecraft().addScheduledTask(new Runnable() {
					@Override
					public void run() {
						String key = message.nbt.getString("key");
						BuildingLib.instance.synBuilding(key, message.nbt);
					}
				});
			} else {
				ctx.getServerHandler().player.world.getMinecraftServer().addScheduledTask(new Runnable() {
					@Override
					public void run() {
						String key = message.nbt.getString("key");
						Building building = BuildingLib.instance.getBuilding(key);
						if (building == null) {
							// 找不到请求的建筑
							return;
						}
						ESNetwork.instance.sendTo(new MessageGetBuilingInfo(key, building),
								ctx.getServerHandler().player);
					}
				});
			}
			return null;
		}

	}
}
