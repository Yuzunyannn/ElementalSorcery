package yuzunyannn.elementalsorcery.util.world;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MapHelper {

	public static class EntityData {

		public Vec3d pos;
		public String name;

		public EntityData(Entity entity) {
			pos = entity.getPositionVector();
			EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
			name = entry == null ? "" : entry.getName();
		}

		public EntityData(ByteBuf buf) {
			readSendData(buf);
		}

		public void writeSendData(ByteBuf buf) {
			buf.writeFloat((float) pos.x);
			buf.writeFloat((float) pos.y);
			buf.writeFloat((float) pos.z);
			ByteBufUtils.writeUTF8String(buf, name);
		}

		public void readSendData(ByteBuf buf) {
			double x = buf.readFloat();
			double y = buf.readFloat();
			double z = buf.readFloat();
			pos = new Vec3d(x, y, z);
			name = ByteBufUtils.readUTF8String(buf);
		}
	}

	public boolean forceLoadChunkEvenNonExistent;

	public int blockUpdateVer;
	public int entityUpdateVer;

	protected BlockPos pos;
	protected int range;
	protected List<EntityData> entityList = new ArrayList<>();

	protected byte[] colors;
	protected int[] datas;

	public MapHelper(BlockPos center, int range) {
		this.pos = center;
		this.range = range;
		this.init();
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getRange() {
		return range;
	}

	public List<EntityData> getEntityList() {
		return entityList;
	}

	public byte[] getColors() {
		return colors;
	}

	public void init() {
		colors = new byte[(range * 2) * (range * 2)];
		datas = new int[(range * 2) * (range * 2)];
	}

	public int getColorByteIndex(int x, int z) {
		return (x + range) + (z + range) * (range * 2);
	}

	public BlockPos getOffsetFromIndex(int i) {
		return new BlockPos(i % (range * 2) - range, 0, i / (range * 2) - range);
	}

	public int getYOffset(int x, int z) {
		int lxIndex = getColorByteIndex(x, z - 1);
		if (lxIndex >= 0 && lxIndex < datas.length) return datas[lxIndex];
		return -1;
	}

	public BlockPos ergodicFunction(int n) {
		return new BlockPos(n / (range * 2) - range, 0, n % (range * 2) - range);
	}

	public void detectBlock(World world) {
		boolean hasChange = false;
		for (int i = 0; i < colors.length; i++) {
			BlockPos p = ergodicFunction(i);
			if (detectAt(world, p.getX(), p.getZ())) hasChange = true;
		}
		if (hasChange) blockUpdateVer++;
	}

	public void writeSendBlockData(ByteBuf buf) {
		buf.writeInt(blockUpdateVer);
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(colors.length);
		buf.writeBytes(colors);
	}

	public void readSendBlockData(ByteBuf buf) {
		blockUpdateVer = buf.readInt();
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		int size = buf.readInt();
		if (colors.length != size) colors = new byte[size];
		buf.readBytes(colors);
	}

	public void detectEntity(World world) {
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, 1000, 1000);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, (e) -> {
			BlockPos pos = e.getPosition();
			int i = getColorByteIndex(pos.getX() - this.pos.getX(), pos.getZ() - this.pos.getZ());
			if (i < 0 || i >= datas.length) return false;
			return datas[i] <= pos.getY() + 4;
		});
		entityList.clear();
		for (EntityLivingBase entity : entities) {
			entityList.add(new EntityData(entity));
		}
		entityUpdateVer++;
	}

	public void writeSendEntityData(ByteBuf buf) {
		buf.writeInt(entityUpdateVer);
		buf.writeInt(entityList.size());
		for (EntityData dat : entityList) dat.writeSendData(buf);
	}

	public void readSendEntityData(ByteBuf buf) {
		entityUpdateVer = buf.readInt();
		int size = buf.readInt();
		entityList.clear();
		for (int i = 0; i < size; i++) entityList.add(new EntityData(buf));
	}

	protected boolean detectAt(World world, int x, int z) {
		int i = getColorByteIndex(x, z);
		if (i < 0 || i >= colors.length) return false;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(this.pos.getX() + x, 255, this.pos.getZ() + z);

		if (!forceLoadChunkEvenNonExistent) {
			IChunkProvider provider = world.getChunkProvider();
			if (!provider.isChunkGeneratedAt(pos.getX() >> 4, pos.getZ() >> 4)) return false;
		}

		Chunk chunk = world.getChunk(pos);
		if (chunk.isEmpty()) return false;

		for (; pos.getY() > 0 && pos.getY() > 0; pos.setY(pos.getY() - 1)) {
			IBlockState state = chunk.getBlockState(pos);
			if (state == Blocks.AIR.getDefaultState()) continue;
			if (state.getBlock() == Blocks.BEDROCK && pos.getY() > this.pos.getY() && pos.getY() > 64) {
				int lockY = pos.getY();
				pos.setY(this.pos.getY());
				state = chunk.getBlockState(pos);
				if (state == Blocks.AIR.getDefaultState()) continue;
				else {
					for (; pos.getY() < lockY; pos.setY(pos.getY() + 1)) {
						state = chunk.getBlockState(pos);
						if (state == Blocks.AIR.getDefaultState()) continue;
					}
				}
			}
			MapColor mapColor = state.getMapColor(world, pos);
			if (mapColor != MapColor.AIR) {
				int lzY = pos.getY();
				if (z - 1 > -range && z - 1 <= range) {
					lzY = getYOffset(x, z - 1);
					if (lzY == -1) lzY = pos.getY();
				}
				byte colorNum = 1;
				if (pos.getY() < lzY - 8) colorNum = 3;
				else if (pos.getY() < lzY) colorNum = 0;
				else if (pos.getY() > lzY) colorNum = 2;
				byte colorByte = (byte) ((mapColor.colorIndex << 2) | colorNum);
				if (colors[i] == colorByte) return false;
				colors[i] = colorByte;
				datas[i] = pos.getY();
				return true;
			}
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public int getColorByColorByteValue(int byteValue) {
		if (byteValue == 0) return 0xff19111d;
		int colorIndex = (byteValue & 0xff) / 4;
		int colorNum = byteValue & 3;
		if (colorIndex < 0 || colorIndex >= MapColor.COLORS.length) return 0xff19111d;
		return MapColor.COLORS[colorIndex].getMapColor(colorNum);
	}

	@SideOnly(Side.CLIENT)
	public void fillDynamicTexture(DynamicTexture texture) {
		int[] buff = texture.getTextureData();
		byte[] colors = this.getColors();
		int len = Math.min(buff.length, colors.length);
		for (int i = 0; i < len; i++) buff[i] = getColorByColorByteValue(colors[i]);
		texture.updateDynamicTexture();
	}

}
