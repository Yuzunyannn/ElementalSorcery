package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.config.WorldGenAndSpawnConfig;

public class WorldGeneratorES {

	static public String[] array(String... str) {
		return str;
	}

	static public int[] array(int... i) {
		return i;
	}

	@Config(kind = "spawn_and_gen", group = "scarlet_crystal_ore", name = "#")
	public static WorldGenAndSpawnConfig CONFIG_SCARLET_CRYSTAL_ORE = new WorldGenAndSpawnConfig(null, null, null,
			null);
	@Config(kind = "spawn_and_gen", group = "kyanite_ore", name = "#")
	public static WorldGenAndSpawnConfig CONFIG_KYANITE_ORE = new WorldGenAndSpawnConfig(null, null, null, null);
	@Config(kind = "spawn_and_gen", group = "start_stone", name = "#")
	public static WorldGenAndSpawnConfig CONFIG_START_STONE = new WorldGenAndSpawnConfig(null, null, null, null);
	@Config(kind = "spawn_and_gen", group = "start_flower", name = "#")
	public static WorldGenAndSpawnConfig CONFIG_START_FLOWER = new WorldGenAndSpawnConfig(null, null, null, null);
	@Config(kind = "spawn_and_gen", group = "seal_stone", name = "#")
	public static WorldGenAndSpawnConfig CONFIG_SEAL_STONE = new WorldGenAndSpawnConfig(
			array("overworld", "the_nether"), null, array(0, -2), null);

	@Config(kind = "spawn_and_gen", group = "elf_tree", name = "#")
	public static WorldGenAndSpawnConfig CONFIG_ELF_TREE = new WorldGenAndSpawnConfig(null, null, null, null);

	// 生成矿
	static public class GenOre {

		private BlockPos pos;

		@SubscribeEvent
		public void onOreGenPost(OreGenEvent.Post event) {
			// 山地会调用两次
			if (event.getPos().equals(this.pos)) return;
			this.pos = event.getPos();
			this.genKynateOre(event);
			this.genScarletCrystalOre(event);
		}

		static void gen(World world, BlockPos pos, Random rand, WorldGenerator generator,
				WorldGenAndSpawnConfig config) {
			Biome biome = world.getBiome(pos);
			if (!config.canSpawn(world, biome)) return;
			if (!TerrainGen.generateOre(world, rand, generator, pos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
				return;
			generator.generate(world, rand, pos);
		}

		private void gen(OreGenEvent.Post event, WorldGenerator generator, WorldGenAndSpawnConfig config) {
			GenOre.gen(event.getWorld(), event.getPos(), event.getRand(), generator, config);
		}

		public void genKynateOre(OreGenEvent.Post event) {
			gen(event, new WorldGenKyaniteOre(), CONFIG_KYANITE_ORE);
		}

		public void genScarletCrystalOre(OreGenEvent.Post event) {
			gen(event, new WorldGenScarletCrystalOre(), CONFIG_SCARLET_CRYSTAL_ORE);
		}
	}

	// 生成装饰
	static public class GenDecorate {

		private BlockPos pos;

		@SubscribeEvent
		public void onDecorateGen(DecorateBiomeEvent.Post event) {
			BlockPos pos = event.getChunkPos().getBlock(0, 0, 0);
			if (pos.equals(this.pos)) return; // 防止重复调用两次
			this.pos = pos;
			this.genElfTree(event);
			gen(event, new WorldGenStarStone(), CONFIG_START_STONE);
			gen(event, new WorldGenSealStone(), CONFIG_SEAL_STONE);
			gen(event, new WorldGenStarFlower(), CONFIG_START_FLOWER);
		}

		// 生成精灵树
		public void genElfTree(DecorateBiomeEvent.Post event) {
			World world = event.getWorld();
			ChunkPos chunkPos = event.getChunkPos();
			Random rand = event.getRand();
			Biome biome = world.getBiome(chunkPos.getBlock(0, 0, 0));
			if (!CONFIG_ELF_TREE.canSpawn(world, biome)) return;
			if (!net.minecraftforge.event.terraingen.TerrainGen.decorate(world, rand, chunkPos,
					DecorateBiomeEvent.Decorate.EventType.CUSTOM))
				return;
			WorldGenerator generator;
			int expect = CONFIG_ELF_TREE.getSpawnPoint(world, biome);
			if (biome == Biomes.PLAINS) {
				if (rand.nextInt(Math.max(1, 5 - expect)) != 0) return;
			} else if (rand.nextInt(Math.max(1, 10 - expect * 2)) != 0) return;
			generator = WorldGenElfTree.getGenTreeFromBiome(false, biome);
			int x = rand.nextInt(16) + 8;
			int z = rand.nextInt(16) + 8;
			BlockPos blockpos = world.getHeight(chunkPos.getBlock(x, 0, z));
			generator.generate(world, rand, blockpos);
		}

		private void gen(DecorateBiomeEvent.Post event, WorldGenerator generator, WorldGenAndSpawnConfig config) {
			BlockPos pos = event.getChunkPos().getBlock(0, 0, 0);
			GenOre.gen(event.getWorld(), pos.add(8, 0, 8), event.getRand(), generator, config);
		}

	}

	// 刷图用
	@Deprecated
	public static class GenTerrain {
		@SubscribeEvent
		public void onPopulate(PopulateChunkEvent.Populate event) {
		}
	}
}
