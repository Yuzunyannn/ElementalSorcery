package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerEnvItem;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;

public class MessageComputerEntity implements IMessage {

	public NBTTagCompound nbt;
	public int itemSlot = -1;
	public int entityId = 0;

	public MessageComputerEntity() {
		nbt = new NBTTagCompound();
	}

	public MessageComputerEntity(Entity entity, NBTTagCompound data, int itemSlot) {
		this.nbt = data;
		this.entityId = entity.getEntityId();
		this.itemSlot = itemSlot;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
		entityId = buf.readInt();
		itemSlot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
		buf.writeInt(entityId);
		buf.writeInt(itemSlot);
	}

	static public class Handler implements IMessageHandler<MessageComputerEntity, IMessage> {
		@Override
		public IMessage onMessage(MessageComputerEntity message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) dealClient(message);
			else dealServer(message, ctx.getServerHandler().player);
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	static public void dealClient(MessageComputerEntity message) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
			toComputer(entity, message.nbt, message.itemSlot);
		});
	}

	static public void dealServer(MessageComputerEntity message, EntityPlayerMP player) {
		final WorldServer world = player.getServerWorld();
		world.addScheduledTask(() -> {
			Entity entity = world.getEntityByID(message.entityId);
			toComputer(entity, message.nbt, message.itemSlot);
		});
	}

	static public void toComputer(Entity entity, NBTTagCompound data, int slot) {
		ItemStack stack = ItemStack.EMPTY;
		IComputEnv env = null;
		if (entity instanceof EntityItem) {
			stack = ((EntityItem) entity).getItem();
			env = new ComputerEnvItem((EntityItem) entity);
		} else if (entity instanceof EntityItemGoods) {
			stack = ((EntityItemGoods) entity).getItem();
			env = new ComputerEnvItem((EntityItemGoods) entity);
		} else if (entity instanceof EntityPlayer) {
			stack = ((EntityPlayer) entity).inventory.getStackInSlot(slot);
			env = new ComputerEnvItem(entity, stack, slot);
		}
		IComputer computer = Computer.from(stack);
		if (computer != null) computer.recvMessage(data, env);
	}
}
