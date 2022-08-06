package yuzunyannn.elementalsorcery.network;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.crack.EffectBlockDisintegrate;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

/** 服务器向客户端同步特效数据 */
public class MessageBlockDisintegrate implements IMessage {

	public static class DisintegratePackage {

		public static final byte DST_TYPE_NONE = 0;
		public static final byte DST_TYPE_POS = 1;
		public static final byte DST_TYPE_ENTITY = 2;

		public BlockPos src;

		public int dstType = DST_TYPE_NONE;
		public int dstE;
		public BlockPos dstP;

		public DisintegratePackage setDst(BlockPos pos) {
			dstType = DST_TYPE_POS;
			dstP = pos;
			return this;
		}

		public DisintegratePackage setDst(Entity entity) {
			dstType = DST_TYPE_ENTITY;
			dstE = entity.getEntityId();
			return this;
		}
	}

	public final List<DisintegratePackage> datas = new ArrayList<>();

	public MessageBlockDisintegrate() {

	}

	public MessageBlockDisintegrate addPackage(DisintegratePackage pkg) {
		datas.add(pkg);
		return this;
	}

	public boolean isEmpty() {
		return datas.isEmpty();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		short size = (short) datas.size();
		buf.writeShort(size);

		for (int i = 0; i < size; i++) {
			DisintegratePackage pkg = datas.get(i);
			// src
			buf.writeInt(pkg.src.getX());
			buf.writeInt(pkg.src.getY());
			buf.writeInt(pkg.src.getZ());
			// dstType
			buf.writeByte(pkg.dstType);
			if (pkg.dstType == DisintegratePackage.DST_TYPE_POS) {
				// posType
				buf.writeInt(pkg.dstP.getX());
				buf.writeInt(pkg.dstP.getY());
				buf.writeInt(pkg.dstP.getZ());
			} else if (pkg.dstType == DisintegratePackage.DST_TYPE_ENTITY) {
				// entityType
				buf.writeInt(pkg.dstE);
			}
			// elements TODO
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int size = buf.readShort();
		datas.clear();
		while (size-- > 0) {
			DisintegratePackage pkg = new DisintegratePackage();
			datas.add(pkg);
			// src
			int x = buf.readInt();
			int y = buf.readInt();
			int z = buf.readInt();
			pkg.src = new BlockPos(x, y, z);
			// dstType
			pkg.dstType = buf.readByte();
			if (pkg.dstType == DisintegratePackage.DST_TYPE_POS) {
				// posType
				x = buf.readInt();
				y = buf.readInt();
				z = buf.readInt();
				pkg.dstP = new BlockPos(x, y, z);
			} else if (pkg.dstType == DisintegratePackage.DST_TYPE_ENTITY) {
				// entityType
				pkg.dstE = buf.readInt();
			}
			// elements TODO
		}

	}

	static public class Handler implements IMessageHandler<MessageBlockDisintegrate, IMessage> {
		@Override
		public IMessage onMessage(MessageBlockDisintegrate message, MessageContext ctx) {
			if (ctx.side != Side.CLIENT) return null;
			final List<DisintegratePackage> datas = message.datas;
			Minecraft.getMinecraft().addScheduledTask(() -> {
				clientUpdate(datas);
			});
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void clientUpdate(List<DisintegratePackage> datas) {
		World world = Minecraft.getMinecraft().world;
		for (DisintegratePackage pkg : datas) {
			BlockPos pos = pkg.src;
			if (world.isAirBlock(pos)) continue;

			IBlockState state = world.getBlockState(pos);
			TileEntity tile = world.getTileEntity(pos);

			world.setBlockState(pos, Blocks.AIR.getDefaultState());

			if (state.getMaterial().isLiquid()) continue;

			Vec3d vec = new Vec3d(pos).add(0.5, 0.5, 0.5);
			EffectBlockDisintegrate effect = new EffectBlockDisintegrate(world, vec, state);

			ElementStack[] elements = ElementMap.instance.toElementStack(ItemHelper.toItemStack(state));
			if (elements != null && elements.length > 0)
				effect.color.setColor(elements[Effect.rand.nextInt(elements.length)].getColor());

			effect.startWaitingTick = Effect.rand.nextInt(10);
			effect.tile = tile;
			if (tile != null) tile.validate();

			Effect.addEffect(effect);
		}
	}

	@Nullable
	public static DisintegratePackage tryBlockDisintegrate(World world, BlockPos pos) {
		if (world.isRemote) return null;
		if (BlockHelper.isBedrock(world, pos)) return null;
		if (world.isAirBlock(pos)) return null;
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1);
		DisintegratePackage pkg = new DisintegratePackage();
		pkg.src = pos;
		return pkg;
	}
}
