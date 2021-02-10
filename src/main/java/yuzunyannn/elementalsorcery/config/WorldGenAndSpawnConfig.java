package yuzunyannn.elementalsorcery.config;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class WorldGenAndSpawnConfig {

	@Config(note = "[会生成的世界][对生物类型无效]")
	protected String[] WORLDS;

	@Config(note = "[会生成的生物群系]")
	protected String[] BIOMES;

	@Config(note = "[生成的密度，对应世界数组，0默认，越大生成的可能越多]")
	protected int[] WORLD_INCRS;

	@Config(note = "[生成的密度，对应维度数组，0默认，越大生成的可能越多]")
	protected int[] BIOME_INCRS;

	public WorldGenAndSpawnConfig(String[] worlds, String[] biomes, int[] worldIncrs, int[] biomeIncrs) {
		this.WORLDS = worlds == null ? new String[] { "overworld" } : worlds;
		this.BIOMES = biomes == null ? new String[0] : biomes;
		this.WORLD_INCRS = worldIncrs == null ? new int[0] : worldIncrs;
		this.BIOME_INCRS = biomeIncrs == null ? new int[0] : biomeIncrs;
	}

	/** 是否可以刷 */
	public boolean canSpawn(World world, Biome biome) {
		for (String name : BIOMES) {
			if (name.equals(biome.getRegistryName().toString())
					|| name.equals(Integer.toString(Biome.getIdForBiome(biome))))
				return true;
		}
		for (String name : WORLDS) {
			if (name.equals(world.provider.getDimensionType().getName())
					|| name.equals(Integer.toString(world.provider.getDimension())))
				return true;
		}
		return false;
	}

	/** 获取刷东西的量，0为默认 */
	public int getSpawnPoint(World world, Biome biome) {
		for (int i = 0; i < BIOMES.length; i++) {
			String name = BIOMES[i];
			if (i >= BIOME_INCRS.length) break;
			if (name.equals(biome.getRegistryName().toString())
					|| name.equals(Integer.toString(Biome.getIdForBiome(biome))))
				return BIOME_INCRS[i];
		}
		for (int i = 0; i < WORLDS.length; i++) {
			String name = WORLDS[i];
			if (i >= WORLD_INCRS.length) break;
			if (name.equals(world.provider.getDimensionType().getName())
					|| name.equals(Integer.toString(world.provider.getDimension())))
				return WORLD_INCRS[i];
		}
		return 0;
	}

	/** 获取所有生物群系 */
	public Entry<Biome, Integer>[] getAllBiomes() {
		ArrayList<Entry<Biome, Integer>> bs = new ArrayList<>();

		for (int i = 0; i < BIOMES.length; i++) {
			String name = BIOMES[i];
			int num = 0;
			if (i < BIOME_INCRS.length) num = BIOME_INCRS[i];
			Biome biome = Biome.REGISTRY.getObject(TextHelper.toResourceLocation(name, "minecraft"));
			if (biome != null) {
				bs.add(new AbstractMap.SimpleEntry(biome, num));
				continue;
			}
			try {
				biome = Biome.getBiome(Integer.parseInt(name));
				if (biome != null) bs.add(new AbstractMap.SimpleEntry(biome, num));
			} catch (Exception e) {}
		}

		return bs.toArray(new Entry[bs.size()]);
	}

}
