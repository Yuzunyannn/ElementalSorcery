package yuzunyannn.elementalsorcery.util.world;

import java.util.Iterator;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.ISyncDispatcher;
import yuzunyannn.elementalsorcery.api.tile.ISyncable;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.util.ds.PackList;
import yuzunyannn.elementalsorcery.util.ds.StaticUnorderList;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.MultipleIterator;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

public class TileFinder implements INBTSS, ISyncable {

	public final static Function<TileEntity, Boolean> ELEMENT_INV = tile -> {
		return tile.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
	};

	public final static Function<TileEntity, Boolean> ALTAR_ELEMENT_INV = tile -> {
		if (tile instanceof IAltarWake) return tile.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		return false;
	};

	public boolean isDirty = false;
	public int tick;

	protected Function<TileEntity, Boolean> fliter = t -> true;
	protected int urgent = -1;
	private BlockPos pos;
	private AxisAlignedBB box;
	private final StaticUnorderList<BlockPos> tilePosList;
	private ISyncDispatcher dispatcher;

	public TileFinder(int limit) {
		this.tilePosList = new StaticUnorderList(limit);
	}

	public TileFinder setBox(AxisAlignedBB box) {
		this.box = box;
		this.pos = new BlockPos(box.getCenter());
		return this;
	}

	public TileFinder setFliter(Function<TileEntity, Boolean> fliter) {
		this.fliter = fliter;
		return this;
	}

	public boolean byDirty() {
		if (isDirty) {
			isDirty = false;
			return true;
		}
		return false;
	}

	public int size() {
		return tilePosList.capacity();
	}

	public int getUseableSize() {
		return tilePosList.size();
	}

	public void urgent(int n) {
		this.urgent = n;
	}

	public TileEntity apply(World world, int loopIndex) {
		if (this.urgent >= 0) this.urgent(20 * 8);
		if (tilePosList.isEmpty()) return null;
		int index = loopIndex % tilePosList.size();
		BlockPos pos = tilePosList.get(index);
		if (pos == null) return null;
		TileEntity tile = null;
		check: {
			TileEntity checked = world.getTileEntity(pos);
			if (checked == null) break check;
			if (checked.isInvalid()) break check;
			if (!this.fliter.apply(checked)) break check;
			tile = checked;
		}
		if (tile == null) remove(index);
		return tile;
	}

	public TileEntity applyUntil(World world, int loopIndex, Function<TileEntity, Boolean> condition) {
		for (int i = 0; i < this.tilePosList.size(); i++) {
			TileEntity tile = apply(world, loopIndex + i);
			if (tile == null) continue;
			if (condition.apply(tile)) return tile;
		}
		return null;
	}

	@Nullable
	public BlockPos get(int index) {
		return tilePosList.get(index);
	}

	public boolean insert(BlockPos pos) {
		if (tilePosList.contains(pos)) return false;
		if (tilePosList.add(pos)) {
			isDirty = true;
			return true;
		}
		return false;
	}

	public void remove(int index) {
		tilePosList.remove(index);
		isDirty = true;
	}

	public void clear() {
		tilePosList.clear();
	}

	public void update(World world) {
		if (world.isRemote) return;
		if (box == null) return;
		if (this.dispatcher != null) {
			if (tick % 10 == 0 && isDirty) {
				isDirty = false;
				this.dispatcher.send(new NBTTagIntArray(toData()));
			}
		}
		tick++;
		if (tilePosList.isFull()) return;
		if (this.urgent > 0) this.urgent--;
		updateFind(world);
	}

	protected Iterator<TileEntity> iter;

	protected void updateFind(World world) {
		boolean isUrgent = this.urgent > 0;
		boolean urgentMode = this.urgent >= 0;
		int capacity = tilePosList.capacity();
		int dtick = 5;
		if (isUrgent);
		else {
			if (!urgentMode) {
				if (tilePosList.isEmpty()) dtick = 10;
				else dtick = 20;
			} else dtick = 20 * 60;
		}
		if (tick % dtick != 0) return;

		if (isUrgent ? true : (tick % 40 != 0)) {
			int width = MathHelper.ceil(Math.max(box.maxX - pos.getX(), box.maxZ - pos.getZ()));
			int height = MathHelper.ceil(box.maxY - pos.getY());
			BlockPos finded = BlockHelper.tryFind(world, (w, b) -> {
				TileEntity tile = w.getTileEntity(b);
				if (tile == null) return false;
				return fliter.apply(tile);
			}, pos, MathHelper.clamp(capacity, 1, 8), width, height);
			if (finded != null) insert(finded);
		}

		if (iter == null) {
			MultipleIterator<TileEntity> iter = new MultipleIterator<>();
			ChunkPos start = new ChunkPos(new BlockPos(box.minX, box.minY, box.minZ));
			ChunkPos end = new ChunkPos(new BlockPos(box.maxX, box.maxY, box.maxZ));
			for (int x = start.x; x <= end.x; x++) {
				for (int z = start.z; z <= end.z; z++) {
					Chunk chunk = world.getChunkProvider().getLoadedChunk(x, z);
					if (chunk == null || chunk.isEmpty()) continue;
					iter.addRandom(chunk.getTileEntityMap().values().iterator());
				}
			}
			this.iter = iter;
		}

		int maxCheckCount = isUrgent ? capacity * 4 : capacity;
		while (iter.hasNext()) {
			TileEntity tile = iter.next();
			if (!MathSupporter.contains(box, tile.getPos())) continue;
			if (!fliter.apply(tile)) continue;
			this.insert(tile.getPos());
			if (tilePosList.isFull()) break;
			if (maxCheckCount-- <= 0) return;
		}

		iter = null;
	}

	public int toRelative(BlockPos pos) {
		if (pos == null) return 0;
		byte dx = (byte) (pos.getX() - this.pos.getX());
		byte dy = (byte) (pos.getY() - this.pos.getY());
		byte dz = (byte) (pos.getZ() - this.pos.getZ());
		return ((dx << 16) & 0xff0000) | ((dy << 8) & 0xff00) | ((dz << 0) & 0xff);
	}

	public BlockPos fromRelative(int n) {
		if (n == 0) return null;
		int dx = (byte) ((n >> 16) & 0xff);
		int dy = (byte) ((n >> 8) & 0xff);
		int dz = (byte) ((n >> 0) & 0xff);
		return new BlockPos(dx + this.pos.getX(), dy + this.pos.getY(), dz + this.pos.getZ());
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		NBTTagList list = new NBTTagList();
		for (BlockPos pos : tilePosList) list.appendTag(new NBTTagIntArray(NBTHelper.toIntArray(pos)));
		writer.write("posList", list);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		this.clear();
		NBTTagList list = reader.listTag("posList", NBTTag.TAG_INT_ARRAY);
		for (int i = 0; i < list.tagCount(); i++) {
			int[] ints = list.getIntArrayAt(i);
			BlockPos pos = NBTHelper.toBlockPos(ints);
			if (pos != BlockPos.ORIGIN) insert(pos);
		}
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writer.write("ls", toData());
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		int[] ints = reader.nints("ls");
		fromData(ints);
	}

	public int[] toData() {
		int[] ints = new int[tilePosList.size()];
		for (int i = 0; i < tilePosList.size(); i++) ints[i] = toRelative(tilePosList.get(i));
		return ints;
	}

	public void fromData(int[] ints) {
		tilePosList.clear();
		int l = Math.min(ints.length, tilePosList.capacity());
		for (int i = 0; i < l; i++) tilePosList.add(fromRelative(ints[i]));
	}

	@Override
	public void setSyncDispatcher(ISyncDispatcher ispatcher) {
		this.dispatcher = ispatcher;
	}

	@Override
	public void onRecvMessage(NBTBase data) {
		if (data.getId() == NBTTag.TAG_INT_ARRAY) {
			int[] ints = ((NBTTagIntArray) data).getIntArray();
			fromData(ints);
		}
	}

	// 注意，这边遍历的不见得精准，但足够用
	public <T> PackList<T> asCapabilityList(World world, Capability<T> capability, T _default) {
		return new PackList(new PackList.IArrayLike<T>() {
			@Override
			public int size() {
				return TileFinder.this.size();
			}

			@Override
			public T get(int index) {
				TileEntity tile = TileFinder.this.apply(world, index);
				if (tile == null) return _default;
				T obj = (T) tile.getCapability(capability, null);
				return obj == null ? _default : obj;
			}
		});
	}

}
