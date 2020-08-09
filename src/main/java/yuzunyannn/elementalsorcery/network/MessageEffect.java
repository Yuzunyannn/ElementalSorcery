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
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.render.particle.Effects;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class MessageEffect implements IMessage {

	public NBTTagCompound nbt;

	public MessageEffect() {
		nbt = new NBTTagCompound();
	}

	public MessageEffect(String name, Vec3d pos, NBTTagCompound nbt) {
		this.nbt = nbt == null ? new NBTTagCompound() : nbt;
		this.nbt.setString("id", name);
		NBTHelper.setPos(this.nbt, "pos", pos);
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
					ElementalSorcery.logger.warn("effect客户端创建出现问题", e);
				}

			});
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void clientUpdate(NBTTagCompound nbt) {
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.player.world;
		Effects.Factory factory = Effects.getFactory(nbt.getString("id"));
		Vec3d pos = NBTHelper.getPos(nbt, "pos");
		factory.show(world, pos, nbt);
	}
}
