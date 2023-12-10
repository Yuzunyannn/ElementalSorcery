package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea.AreaExcerpt;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class DungeonWorld extends WorldSavedData {

	@Config(kind = "dungeon")
	public static boolean ENABLE_DUNGEON = true;

	@Config(kind = "dungeon")
	@Config.NumberRange(max = 1000, min = 0)
	public static int DUNGEON_COMPENSATE_WHEN_DISABLE = 0;

	@Config(kind = "dungeon")
	public static double DUNGEON_BUILDING_BLOCK_SPEED_LIMIT = 2;

	@Config(kind = "dungeon")
	public static double DUNGEON_BUILDING_CLEAR_SPEED_LIMIT = -1;

	/** 获取地牢对象 */
	public static DungeonWorld getDungeonWorld(World world) {
		if (world.isRemote) return getDungeonWorldClient(world);
		MapStorage storage = world.getMapStorage();
		WorldSavedData worldSave = storage.getOrLoadData(DungeonWorld.class, "ESDungeonWorld");
		if (worldSave == null) {
			worldSave = new DungeonWorld("ESDungeonWorld");
			storage.setData("ESDungeonWorld", worldSave);
		}
		DungeonWorld dw = (DungeonWorld) worldSave;
		dw.world = world;
		return dw;
	}

	@SideOnly(Side.CLIENT)
	protected static DungeonWorld getDungeonWorldClient(World world) {
		return DungeonWorldClient.getDungeonWorld(world);
	}

	public static class DungeonWorldLand extends WorldSavedData {

		protected List<AreaExcerpt> excerpts = new ArrayList<>();
		protected Map<DungeonPos, AreaExcerpt> excerptMap = new HashMap<>();

		public DungeonWorldLand(String name) {
			super(name);
		}

		protected void updateByExcerpt(AreaExcerpt excerpt) {
			for (int x = excerpt.minAX; x <= excerpt.maxAX; x++) {
				for (int z = excerpt.minAZ; z <= excerpt.maxAZ; z++) {
					DungeonPos pos = new DungeonPos(x, z);
					if (excerptMap.containsKey(pos)) ESAPI.logger.warn("重复的Dungeon");
					excerptMap.put(pos, excerpt);
				}
			}
		}

		public boolean isCross(AreaExcerpt excerpt) {
			for (int x = excerpt.minAX; x <= excerpt.maxAX; x++) {
				for (int z = excerpt.minAZ; z <= excerpt.maxAZ; z++) {
					DungeonPos pos = new DungeonPos(x, z);
					if (excerptMap.containsKey(pos)) return true;
				}
			}
			return false;
		}

		public AreaExcerpt findExcerpt(DungeonPos pos) {
			return excerptMap.get(pos);
		}

		protected void add(AreaExcerpt excerpt) {
			excerpts.add(excerpt);
			updateByExcerpt(excerpt);
			this.markDirty();
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			NBTHelper.setNBTSerializableList(compound, "excerpts", excerpts);
			return compound;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			excerpts = NBTHelper.getNBTSerializableList(nbt, "excerpts", AreaExcerpt.class, NBTTagCompound.class);
			excerptMap.clear();
			for (AreaExcerpt excerpt : excerpts) updateByExcerpt(excerpt);
		}

	}

	public static final int LAND_SPLIT = 32; // 在 CHUNK_SPLIT 上继续分割

	protected World world;
	protected int idCounter = 1;
	protected Map<DungeonPos, DungeonWorldLand> wlMap = new HashMap<>();

	public DungeonWorld(String name) {
		super(name);
	}

	public DungeonWorld() {
		super("ESDungeonWorld");
	}

	@Nullable
	public DungeonArea getDungeon(int id) {
		MapStorage storage = world.getMapStorage();
		String key = "ESDungeon_" + String.valueOf(id);
		WorldSavedData worldSave = storage.getOrLoadData(DungeonArea.class, key);
		return (DungeonArea) worldSave;
	}

	public DungeonArea newDungeon(BlockPos pos) {
		int id = idCounter;

		String key = "ESDungeon_" + String.valueOf(id);
		DungeonArea worldSave = new DungeonArea(key);
		AreaExcerpt excerpt = worldSave.excerpt;
		excerpt.id = id;
		try {
			worldSave.generate(this, pos);
		} catch (DungeonTooManyBuildException e) {
			ESAPI.logger.warn("dungeon build to many!", e);
			return worldSave.setFailMsg("info.dungeon.build.too.many");
		} catch (Exception e) {
			ESAPI.logger.warn("dungeon build fail!", e);
			return worldSave.setFailMsg("info.dungeon.build.exception");
		}

		if (isCrossWithOther(excerpt)) return worldSave.setFailMsg("info.dungeon.build.too.close");

		MapStorage storage = world.getMapStorage();
		storage.setData(key, worldSave);

		addToLand(excerpt);
		idCounter++;
		this.markDirty();

		return (DungeonArea) worldSave;
	}

	protected DungeonWorldLand getOrCreateWorldLand(int x, int z) {
		DungeonPos pos = new DungeonPos(x, z);
		String key = String.format("ESDungeonLand_%d_%d", x, z);
		MapStorage storage = world.getMapStorage();
		DungeonWorldLand worldSave = (DungeonWorldLand) storage.getOrLoadData(DungeonWorldLand.class, key);
		if (worldSave == null) {
			worldSave = new DungeonWorldLand(key);
			storage.setData(key, worldSave);
		}
		wlMap.put(pos, worldSave);
		return worldSave;
	}

	@Nullable
	public DungeonWorldLand getWorldLand(int x, int z) {
		DungeonPos pos = new DungeonPos(x, z);
		if (wlMap.containsKey(pos)) return wlMap.get(pos);
		String key = String.format("ESDungeonLand_%d_%d", x, z);
		MapStorage storage = world.getMapStorage();
		DungeonWorldLand worldSave = (DungeonWorldLand) storage.getOrLoadData(DungeonWorldLand.class, key);
		wlMap.put(pos, worldSave);
		return worldSave;
	}

	protected void addToLand(AreaExcerpt excerpt) {
		for (int x = excerpt.minAX / LAND_SPLIT; x <= excerpt.maxAX / LAND_SPLIT; x++) {
			for (int z = excerpt.minAZ / LAND_SPLIT; z <= excerpt.maxAZ / LAND_SPLIT; z++) {
				getOrCreateWorldLand(x, z).add(excerpt);
			}
		}
	}

	public boolean isCrossWithOther(AreaExcerpt excerpt) {
		for (int x = excerpt.minAX / LAND_SPLIT; x <= excerpt.maxAX / LAND_SPLIT; x++) {
			for (int z = excerpt.minAZ / LAND_SPLIT; z <= excerpt.maxAZ / LAND_SPLIT; z++) {
				DungeonWorldLand land = getWorldLand(x, z);
				if (land != null && land.isCross(excerpt)) return true;
			}
		}
		return false;
	}

	@Nullable
	public AreaExcerpt getAreaExcerpt(ChunkPos pos) {
		int x = pos.x / DungeonArea.CHUNK_SPLIT;
		int z = pos.z / DungeonArea.CHUNK_SPLIT;
		DungeonWorldLand land = getWorldLand(x / LAND_SPLIT, z / LAND_SPLIT);
		if (land != null) return land.findExcerpt(new DungeonPos(x, z));
		return null;
	}

	@Nullable
	public DungeonAreaRoom getAreaRoom(BlockPos pos) {
		AreaExcerpt excerpt = getAreaExcerpt(new ChunkPos(pos));
		if (excerpt == null) return null;
		DungeonArea dungeon = getDungeon(excerpt.id);
		if (dungeon == null) return null;
		return dungeon.findRoom(new Vec3d(pos).add(-0.25, -0.25, -0.25));
	}

	public void markDirty(DungeonAreaRoom room) {
		DungeonArea dungeon = this.getDungeon(room.getAreId());
		if (dungeon == null) return;
		dungeon.markDirty();
		room.nextUpdateFlag();
	}

	public void debugClear() {
		if (!ESAPI.isDevelop) return;
		this.idCounter = 1;
		for (DungeonWorldLand land : wlMap.values()) {
			land.excerptMap.clear();
			land.excerpts.clear();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		idCounter = nbt.getInteger("idCounter");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("idCounter", idCounter);
		return nbt;
	}

}
