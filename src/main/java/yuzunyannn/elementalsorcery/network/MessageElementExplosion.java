package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;

public class MessageElementExplosion implements IMessage {

	public ElementStack estack = ElementStack.EMPTY;
	public float x, y, z;
	public int seed;

	public MessageElementExplosion() {
	}

	public MessageElementExplosion(ElementStack estack, Vec3d pos, int seed) {
		this.estack = estack;
		this.x = (float) pos.x;
		this.y = (float) pos.y;
		this.z = (float) pos.z;
		this.seed = seed;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		Element element = Element.getElementFromId(buf.readInt());
		int count = buf.readInt();
		int power = buf.readInt();
		estack = new ElementStack(element, count, power);
		this.x = buf.readFloat();
		this.y = buf.readFloat();
		this.z = buf.readFloat();
		this.seed = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(Element.getIdFromElement(estack.getElement()));
		buf.writeInt(estack.getCount());
		buf.writeInt(estack.getPower());
		buf.writeFloat(x);
		buf.writeFloat(y);
		buf.writeFloat(z);
		buf.writeInt(seed);
	}

	static public class Handler implements IMessageHandler<MessageElementExplosion, IMessage> {
		@Override
		public IMessage onMessage(MessageElementExplosion message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) dealClient(message, ctx);
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	static public void dealClient(MessageElementExplosion data, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			ElementExplosion.doExplosionClient(Minecraft.getMinecraft().world, data);
		});
	}

}
