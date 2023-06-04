package yuzunyannn.elementalsorcery.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonPos;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld.DungeonWorldLand;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorldClient;

public class MessageDungeonSync implements IMessage {

	public NBTTagCompound nbt;

	public MessageDungeonSync() {
		nbt = new NBTTagCompound();
	}

	public MessageDungeonSync(DungeonPos pos, DungeonWorldLand land) {
		nbt = new NBTTagCompound();
		nbt.setInteger("pX", pos.x);
		nbt.setInteger("pZ", pos.z);
		nbt.setTag("land", land.serializeNBT());
	}

	public MessageDungeonSync(DungeonArea area) {
		nbt = new NBTTagCompound();
		nbt.setInteger("dId", area.getExcerpt().getId());
		nbt.setTag("area", area.serializeToClient());
	}

	public MessageDungeonSync(DungeonAreaRoom room) {
		nbt = new NBTTagCompound();
		nbt.setInteger("dId", room.getAreId());
		nbt.setTag("room", room.serializeToClient());
	}

	@SideOnly(Side.CLIENT)
	public MessageDungeonSync(DungeonPos pos) {
		nbt = new NBTTagCompound();
		nbt.setInteger("pX", pos.x);
		nbt.setInteger("pZ", pos.z);
	}

	@SideOnly(Side.CLIENT)
	public MessageDungeonSync(int id) {
		nbt = new NBTTagCompound();
		nbt.setInteger("dId", id);
	}

	@SideOnly(Side.CLIENT)
	public MessageDungeonSync(int areaId, int roomId) {
		nbt = new NBTTagCompound();
		nbt.setInteger("dId", areaId);
		nbt.setInteger("rId", roomId);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	static public class Handler implements IMessageHandler<MessageDungeonSync, IMessage> {
		@Override
		public IMessage onMessage(MessageDungeonSync message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) dealClient(message.nbt);
			else dealServer(message.nbt, ctx.getServerHandler().player);
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	static public void dealClient(NBTTagCompound data) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			DungeonWorldClient dw = DungeonWorldClient.getDungeonWorld(Minecraft.getMinecraft().world);
			if (dw == null) return;
			if (data.hasKey("dId")) {
				if (data.hasKey("room")) {
					NBTTagCompound roomData = data.getCompoundTag("room");
					int areaId = data.getInteger("dId");
					int roomId = roomData.getInteger("id");
					DungeonArea area = dw.getDungeonRaw(areaId);
					if (area == null) return;
					DungeonAreaRoom room = area.getRoomById(roomId);
					if (room == null) return;
					room.deserializeInClient(roomData);
				} else {
					DungeonArea area = new DungeonArea("");
					area.deserializeInClient(data.getCompoundTag("area"));
					dw.setDungeonArea(data.getInteger("dId"), area);
				}
			} else {
				DungeonPos pos = new DungeonPos(data.getInteger("pX"), data.getInteger("pZ"));
				DungeonWorldLand land = new DungeonWorldLand("");
				land.readFromNBT(data.getCompoundTag("land"));
				dw.setWorldLand(pos, land);
			}
		});
	}

	static public void dealServer(NBTTagCompound data, EntityPlayerMP player) {
		final WorldServer world = player.getServerWorld();
		world.addScheduledTask(() -> {
			DungeonWorld dw = DungeonWorld.getDungeonWorld(world);
			if (data.hasKey("dId")) {
				DungeonArea area = dw.getDungeon(data.getInteger("dId"));
				if (area == null) return;
				if (data.hasKey("rId")) {
					DungeonAreaRoom room = area.getRoomById(data.getInteger("rId"));
					if (room == null) return;
					ESNetwork.instance.sendTo(new MessageDungeonSync(room), player);
				} else ESNetwork.instance.sendTo(new MessageDungeonSync(area), player);
			} else {
				DungeonPos pos = new DungeonPos(data.getInteger("pX"), data.getInteger("pZ"));
				DungeonWorldLand land = dw.getWorldLand(pos.x, pos.z);
				if (land == null) return;
				ESNetwork.instance.sendTo(new MessageDungeonSync(pos, land), player);
			}
		});
	}
}
