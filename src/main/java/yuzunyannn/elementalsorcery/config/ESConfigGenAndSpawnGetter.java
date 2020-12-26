package yuzunyannn.elementalsorcery.config;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

public class ESConfigGenAndSpawnGetter {

	private ArrayList<Entry<String, Integer>> worlds = new ArrayList<>();
	private ArrayList<Entry<String, Integer>> biomes = new ArrayList<>();

	public ESConfigGenAndSpawnGetter(String string) {
		if (string == null) return;
		try {
			String[] vals = string.split("\\|");
			for (String val : vals) {
				Pattern p = Pattern.compile("(.+)\\$([^\\(\\)]+)(.*)");
				Matcher m = p.matcher(val);
				while (m.find()) {
					String type = m.group(1).trim().toLowerCase();
					String name = m.group(2).trim();
					String extra = m.group(3).trim();
					parse(type, name, extra);
				}
			}
		} catch (Exception e) {
			ElementalSorcery.logger.warn("解析生成配置异常！" + string);
		}
	}

	protected void parse(String type, String name, String extra) {
		if (name.isEmpty()) return;
		Integer num = null;
		Pattern p = Pattern.compile("\\((\\d+)\\)");
		Matcher m = p.matcher(extra);
		if (m.find()) num = Integer.parseInt(m.group(1));

		switch (type) {
		case "world":
			worlds.add(new AbstractMap.SimpleEntry(name, num));
			break;
		case "biome":
			biomes.add(new AbstractMap.SimpleEntry(name, num));
			break;
		default:
			break;
		}
	}

	/** 是否可以刷 */
	public boolean canSpawn(World world, Biome biome) {
		for (Entry<String, Integer> entry : biomes) {
			String name = entry.getKey();
			if (name.equals(biome.getRegistryName().toString())
					|| name.equals(Integer.toString(Biome.getIdForBiome(biome))))
				return true;
		}
		for (Entry<String, Integer> entry : worlds) {
			String name = entry.getKey();
			if (name.equals(world.provider.getDimensionType().getName())
					|| name.equals(Integer.toString(world.provider.getDimension())))
				return true;
		}
		return false;
	}

	/** 获取刷东西的量，0为默认 */
	public int getSpawnPoint(World world, Biome biome) {
		for (Entry<String, Integer> entry : biomes) {
			String name = entry.getKey();
			Integer num = entry.getValue();
			if (name.equals(biome.getRegistryName().toString())
					|| name.equals(Integer.toString(Biome.getIdForBiome(biome))))
				return num == null ? 0 : num;
		}
		for (Entry<String, Integer> entry : worlds) {
			String name = entry.getKey();
			Integer num = entry.getValue();
			if (name.equals(world.provider.getDimensionType().getName())
					|| name.equals(Integer.toString(world.provider.getDimension())))
				return num == null ? 0 : num;
		}
		return 0;
	}

	/** 获取所有生物群系 */
	public Entry<Biome, Integer>[] getAllBiomes() {
		ArrayList<Entry<Biome, Integer>> bs = new ArrayList<>();
		for (Entry<String, Integer> entry : biomes) {
			String name = entry.getKey();
			Integer num = entry.getValue();
			num = num == null ? 0 : num;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("worlds:").append(worlds);
		sb.append("biomes:").append(biomes);
		return sb.toString();
	}

}
