package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

/** 服务器向客户端同步特效数据 */
public class MessageEffect implements IMessage {

	public NBTTagCompound nbt;

	public MessageEffect() {
		nbt = new NBTTagCompound();
	}

	public MessageEffect(int id, Vec3d pos, NBTTagCompound nbt) {
		this.nbt = nbt == null ? new NBTTagCompound() : nbt;
//		this.nbt.setInteger("id", id);
		this.nbt.setByte("id", (byte) id);// 目前用不到int，所以发送byte
		NBTHelper.setVec3d(this.nbt, "pos", pos);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	static public class Handler implements IMessageHandler<MessageEffect, IMessage> {
		@Override
		public IMessage onMessage(MessageEffect message, MessageContext ctx) {
			if (ctx.side != Side.CLIENT) return null;
			final NBTTagCompound nbt = message.nbt;
			Minecraft.getMinecraft().addScheduledTask(() -> {
				try {
					clientUpdate(nbt);
				} catch (Exception e) {
					ESAPI.logger.warn("effect客户端创建出现问题", e);
				}

			});
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void clientUpdate(NBTTagCompound nbt) {
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.player.world;
		Effects.Factory factory = Effects.getFactory(nbt.getInteger("id"));
		Vec3d pos = NBTHelper.getVec3d(nbt, "pos");
		factory.show(world, pos, nbt);
	}
}
