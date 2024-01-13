package yuzunyannn.elementalsorcery.logics;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.building.BlockItemTypeInfo;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DevelopStatistics {

	public static void record(World world, int size, ChunkPos origin) {
		DevelopStatistics.Record r = new DevelopStatistics.Record(world, size, origin);
//		GameHelper.clientRun(() -> {
//			EventClient.addTickTask(() -> {
//				synchronized (r) {
//					try {
//						r.wait();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				return ITickTask.END;
//			});
//		});
		EventServer.addTickTask(r);
	}

	public static class Record implements ITickTask {

		public int x, z;
		public int at;
		public final ChunkPos origin;
		public final int size;
		public final int total;
		public final World world;

		public Record(World world, int size, ChunkPos origin) {
			this.size = size;
			this.total = (size + 1 + size) * (size + 1 + size);
			this.origin = origin;
			this.world = world;
			x = -size;
			z = -size;
		}

		@Override
		public int onTick() {
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < this.total * 0.01f; i++) {
				ChunkPos chuckPos = new ChunkPos(origin.x + x, origin.z + z);
				DevelopStatistics.statistics(world, chuckPos);
				at = at + 1;
				x = x + 1;
				if (x > size) {
					x = -size;
					z = z + 1;
					if (z > size) {
						ESAPI.logger.info("记录完成");
						DevelopStatistics.saveAll();
						DevelopStatistics.handleResult();
//						synchronized (this) {
//							this.notifyAll();
//						}
						return ITickTask.END;
					}
				}
			}
			long endTime = System.currentTimeMillis();
			ESAPI.logger.info((100 * at / (float) total) + "%" + "统计进度，耗时" + (endTime - startTime) + "ms");
			return ITickTask.SUCCESS;
		}

	}

	public static final Map<String, JsonObject> map = new HashMap<>();

	public static final String FILE_STATISTICS = "statistics.json";
	public static final String FILE_STATISTICS_TYPE = "statistics-map.json";

	public static final String FILE_FILTER_WORLD = "statistics-filter-world.json";
	public static final String FILE_FILTER_BIOME = "statistics-filter-biome.json";
	public static final String FILE_FILTER_ALL = "statistics-filter-all.json";
	public static final String FILE_FILTER_WORTH = "statistics-filter-worth.json";

	public static JsonObject loadFile(File file) {
		try {
			return new JsonObject(file);
		} catch (IOException e) {
			return new JsonObject();
		}
	}

	public static void saveFile(File file, JsonObject json) {
		json.save(file, true);
	}

	public static JsonObject loadFile(String file) {
		return loadFile(ElementalSorcery.data.getFile("develop", file));
	}

	public static void saveFile(String file, JsonObject json) {
		saveFile(ElementalSorcery.data.getFile("develop", file), json);
	}

	public static JsonObject getData(String file) {
		JsonObject jobj = map.get(file);
		if (jobj == null) map.put(file, jobj = loadFile(file));
		return jobj;
	}

	public static void saveFile(String file) {
		JsonObject jobj = map.get(file);
		if (jobj != null) saveFile(file, jobj);
	}

	public static void clearAll() {
		map.clear();
	}

	public static void saveAll() {
		saveFile(FILE_STATISTICS);
		saveFile(FILE_STATISTICS_TYPE);
	}

	private static JsonObject toType(ItemStack stack) {
		JsonObject json = new JsonObject();
		json.set("item", stack.getItem().getRegistryName().toString());
		if (stack.getHasSubtypes()) json.set("data", stack.getMetadata());
		return json;
	}

	public static void statistics(World world, ChunkPos chuckPos) {
		JsonObject data = getData(FILE_STATISTICS);
		JsonObject typeMap = getData(FILE_STATISTICS_TYPE);

		world.getChunk(chuckPos.x, chuckPos.z);
		data = data.getOrCreateObject(Long.toString(world.getSeed()));
		data = data.getOrCreateObject(world.getBiome(chuckPos.getBlock(0, 0, 0)).getRegistryName().toString());

		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 200; y++) {
				for (int z = 0; z < 16; z++) {
					BlockPos pos = chuckPos.getBlock(x, y, z);
					if (world.isAirBlock(pos)) continue;
					IBlockState state = world.getBlockState(pos);
					BlockItemTypeInfo info = new BlockItemTypeInfo(state);
					ItemStack itemStack = info.getItemStack();
					if (itemStack.isEmpty()) continue;
					String key = itemStack.getDisplayName();
					long origin = 0;
					if (data.hasNumber(key)) origin = data.getNumber(key).longValue();
					data.set(key, origin + 1);
					typeMap.set(key, toType(itemStack));
				}
			}
		}
	}

	public static void handleResult() {
		JsonObject data = getData(FILE_STATISTICS);

		JsonObject filterWorth = new JsonObject();
		JsonObject filterAll = new JsonObject();
		JsonObject filterWorld = new JsonObject();
		JsonObject filterBiome = new JsonObject();

		String maxItem = null;
		long max = 0;

		for (String world : data) {
			JsonObject worldJson = data.getObject(world);
			for (String biome : worldJson) {
				JsonObject biomeJson = worldJson.getObject(biome);
				for (String item : biomeJson) {
					long count = biomeJson.getNumber(item).longValue();
					long newCount = filterAll.getNumber(item, 0).longValue() + count;
					filterAll.set(item, newCount);
					if (newCount > max) {
						max = newCount;
						maxItem = item;
					}
					JsonObject fWorld = filterWorld.getOrCreateObject(world);
					fWorld.set(item, fWorld.getNumber(item, 0).longValue() + count);

					JsonObject fBiome = filterBiome.getOrCreateObject(biome);
					fBiome.set(item, fBiome.getNumber(item, 0).longValue() + count);
				}
			}
		}

		if (maxItem != null) {

		}
		
		for (String item : filterAll) {
			long count = filterAll.getNumber(item, 0).longValue();
			if (count == 0) continue;
			filterWorth.set(item, MathHelper.ceil(max / count));
		}

		saveFile(FILE_FILTER_WORLD, filterWorld);
		saveFile(FILE_FILTER_BIOME, filterBiome);
		saveFile(FILE_FILTER_ALL, filterAll);
		saveFile(FILE_FILTER_WORTH, filterWorth);

	
	}

}
