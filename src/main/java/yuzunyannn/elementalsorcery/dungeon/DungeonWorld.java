package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea.AreaExcerpt;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class DungeonWorld extends WorldSavedData {

	/** 获取地牢对象 */
	public static DungeonWorld getDungeonWorld(World world) {
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

	protected World world;
	protected int idCounter = 1;
	protected List<AreaExcerpt> excerpts = new ArrayList<>();
	protected Set<String> landSet = new HashSet<>();

	public DungeonWorld(String name) {
		super(name);
	}

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
		worldSave.generate(this, pos);
		if (isLandCross(excerpt)) return worldSave.setFailMsg("");

		MapStorage storage = world.getMapStorage();
		storage.setData(key, worldSave);
		excerpts.add(excerpt);
		updateLandSet(excerpt);
		idCounter++;

		return (DungeonArea) worldSave;
	}

	public void debugClear() {
		if (!ESAPI.isDevelop) return;
		this.idCounter = 1;
		this.excerpts.clear();
		this.landSet.clear();
	}

	protected void updateLandSet(AreaExcerpt excerpt) {
		for (int x = excerpt.minAX; x <= excerpt.maxAX; x++) {
			for (int z = excerpt.minAZ; z <= excerpt.maxAZ; z++) {
				String landKey = x + "_" + z;
				landSet.add(landKey);
			}
		}
	}

	protected boolean isLandCross(AreaExcerpt excerpt) {
		for (int x = excerpt.minAX; x <= excerpt.maxAX; x++) {
			for (int z = excerpt.minAZ; z <= excerpt.maxAZ; z++) {
				String landKey = x + "_" + z;
				if (landSet.contains(landKey)) return true;
			}
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		idCounter = nbt.getInteger("idCounter");
		excerpts = NBTHelper.getNBTSerializableList(nbt, "excerpts", AreaExcerpt.class, NBTTagCompound.class);
		landSet.clear();
		for (AreaExcerpt excerpt : excerpts) updateLandSet(excerpt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("idCounter", idCounter);
		NBTHelper.setNBTSerializableList(nbt, "excerpt", excerpts);
		return nbt;
	}

}
